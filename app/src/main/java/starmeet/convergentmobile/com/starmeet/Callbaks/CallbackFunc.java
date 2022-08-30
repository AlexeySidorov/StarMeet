package starmeet.convergentmobile.com.starmeet.Callbaks;

/**
 * Created by alexeysidorov on 26.02.2018.
 */

public interface CallbackFunc<T> {
    //Успешный ответ
    void onSuccess(T result);

    //Ошибка
    void OnError(String error);
}
