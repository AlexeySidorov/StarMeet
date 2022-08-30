package starmeet.convergentmobile.com.starmeet.Callbaks;


public interface CallbackEmpty {
    //Успешный ответ
    void onSuccess();

    //Не корректный ответ
    void onFailure(Throwable e);
}
