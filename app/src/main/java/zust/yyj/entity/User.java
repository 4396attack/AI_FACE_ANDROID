package zust.yyj.entity;

public class User {
    private Integer id;
    private String name;
    private String birthday;
    private String realName;
    private Integer age;
    private String pwd;
    private String phone;
    private Integer deleted;

    public User(){
        super();
    }
    public User(String name, String realName, Integer age,String phone, String pwd) {
        this.name = name;
        this.realName = realName;
        this.age = age;
        this.pwd = pwd;
        this.deleted = 0;
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "User [ id = "+ id +" ,name = "+ name +" ,birthday = "+ birthday +" ,realName = "+ realName +" ,age = "+ age +" ,pwd = "+ pwd +" ,phone = "+ phone +" ,deleted = "+ deleted +" ]";
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
