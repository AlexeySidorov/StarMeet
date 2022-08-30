package starmeet.convergentmobile.com.starmeet.Callbaks;


public interface Callback<T> {
    //Успешный ответ
    void onSuccess(T result);

    //Не корректный ответ
    void onFailure(Throwable e);

    //Ошибка
    void OnError(String error);
}
