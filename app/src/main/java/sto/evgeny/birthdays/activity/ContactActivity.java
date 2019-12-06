package sto.evgeny.birthdays.activity;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import sto.evgeny.birthdays.ExtraKey;
import sto.evgeny.birthdays.model.ListElement;
import sto.evgeny.birthdays.R;
import sto.evgeny.birthdays.task.Callback;
import sto.evgeny.birthdays.task.GetEmailsTask;
import sto.evgeny.birthdays.task.GetPhonesTask;

public class ContactActivity extends AppCompatActivity {

    private List<ListElement> elemList;
    private RecyclerView.Adapter<ItemViewHolder> adapter;
    private int runningTasks;
    private long lastClicked;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact);

        long cId = getIntent().getLongExtra(ExtraKey.CONTACT_ID.name(), -1L);
		String cName = getIntent().getStringExtra(ExtraKey.CONTACT_NAME.name());
		
		((TextView) findViewById(R.id.contactNameText)).setText(cName);

        RecyclerView detailed = findViewById(R.id.detailedInfo);
        detailed.setLayoutManager(new LinearLayoutManager(this));
		
		elemList = new ArrayList<>();
		
		adapter = new RecyclerView.Adapter<ItemViewHolder>() {

			@Override
			public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
				View view = LayoutInflater.from(parent.getContext()).inflate(
						R.layout.detail_item, parent, false);
				view.setClickable(true);
				view.setFocusable(true);
				return new ItemViewHolder(view);
			}

			@Override
			public void onBindViewHolder(final ItemViewHolder holder, int position) {
				TextView textView = holder.getView().findViewById(R.id.detailItemText);
				View separator = holder.getView().findViewById(R.id.detailItemSeparator);
				textView.setText(elemList.get(position).getText());
				boolean isGroupHeader = elemList.get(position).getType() == ListElement.Type.GROUP_HEADER;
				int textColor = ContextCompat.getColor(ContactActivity.this,
						isGroupHeader ? R.color.green : R.color.gray);
				int separatorColor = ContextCompat.getColor(ContactActivity.this,
						isGroupHeader ? R.color.green : android.R.color.transparent);
				textView.setTextColor(textColor);
				separator.setBackgroundColor(separatorColor);

				if (!holder.getView().hasOnClickListeners()) {
                    holder.getView().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            final long time = System.currentTimeMillis();
                        	lastClicked = time;
                            int fromColor = ContextCompat.getColor(ContactActivity.this, android.R.color.background_light);
                            int toColor = ContextCompat.getColor(ContactActivity.this, R.color.lightGray);
                            ValueAnimator animator = ValueAnimator.ofArgb(fromColor, toColor, fromColor);
                            animator.setDuration(250);
                            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    v.setBackgroundColor((int) animation.getAnimatedValue());
                                    if (animation.getAnimatedFraction() == 1 && lastClicked == time) {
                                    	ListElement.Type type = elemList.get(holder.getAdapterPosition()).getType();
                                        String action = type.getAction();
                                        if (action == null) {
                                        	return;
										}
										String prefix = type.getPrefix();
										Intent intent = new Intent(action);
                                        intent.setData(Uri.parse(prefix +
												elemList.get(holder.getAdapterPosition()).getText()));
                                        startActivity(intent);
                                    }
                                }
                            });
                            animator.start();
                        }
                    });
                }
			}

			@Override
			public int getItemCount() {
				return elemList.size();
			}
		};
		
		detailed.setAdapter(adapter);

        Callback callback = new Callback() {
            @Override
            public synchronized void onSuccess(int taskId, List<ListElement> result) {
                elemList.addAll(result);
                adapter.notifyDataSetChanged();
                runningTasks = runningTasks ^ taskId;
                if (runningTasks == 0) {
                    ContactActivity.this.findViewById(R.id.progressBarCircle).setVisibility(View.GONE);
                }
            }
        };

        new GetPhonesTask(this, 0b01, callback).execute(String.valueOf(cId));
		new GetEmailsTask(this, 0b10, callback).execute(String.valueOf(cId));
        runningTasks = 0b11;
	}

	public static class ItemViewHolder extends RecyclerView.ViewHolder {

    	private View view;

		ItemViewHolder(View view) {
			super(view);
			this.view = view;
		}

		View getView() {
			return view;
		}
	}
}
