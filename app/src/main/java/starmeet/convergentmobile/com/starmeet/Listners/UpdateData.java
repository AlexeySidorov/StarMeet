package starmeet.convergentmobile.com.starmeet.Listners;

/**
 * Created by alexeysidorov on 04.03.2018.
 */

public class UpdateData {
    public interface UpdateListenerByModel<T>{
        void Update(T model);
    }

    public interface UpdateListener{
        void Update();
    }
}
