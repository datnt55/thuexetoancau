package grab.com.thuexetoancau.model;

/**
 * Created by DatNT on 7/29/2017.
 */

public class Phone {
    private String code;
    private String fullName;
    private String number;
    private  int flag;
    public Phone() {
    }

    public Phone(String code, String fullName, String number, int flag) {
        this.code = code;
        this.fullName = fullName;
        this.number = number;
        this.flag = flag;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }
}
