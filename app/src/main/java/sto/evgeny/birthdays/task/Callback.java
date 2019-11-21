package sto.evgeny.birthdays.task;

import java.util.List;

import sto.evgeny.birthdays.ListElement;

public interface Callback {
    void onSuccess(int taskId, List<ListElement> result);
}
