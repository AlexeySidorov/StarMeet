package starmeet.convergentmobile.com.starmeet.Listners;

/**
 * Created by alexeysidorov on 26.03.2018.
 */

public interface AdapterClickListener<T> {
    void ItemClick(T item);
    void ElementItemClick(T item, int elementId);
}
