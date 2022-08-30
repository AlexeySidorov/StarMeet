package starmeet.convergentmobile.com.starmeet.Models;

public enum PaymentType {
    None(0),
    PayTm(1),
    Card(2),
    Info(3);

    PaymentType(int i) {
        this.type = i;
    }

    private int type;

    public int getNumericType() {
        return type;
    }
}
