package utils;

public class ErrorCodes {
    public final static Integer SUCCESS = 0;

    public final static Integer NOT_LOGIN = 1;

    public final static Integer INVALID_PARAM = 2;

    public final static Integer ALREADYEXIT = 3; // 手机号已被注册

    public final static Integer NO_SUCH_USER = 4;//账户名或密码错误

    public final static Integer NO_SUCH_PHOTO = 5;//该照片不存在

    public final static Integer DB_INSERT_ERR = 6;//数据库插入失败
    public final static Integer DB_UPDATE_ERR = 7;//数据库更新失败
    public final static Integer DB_DELETED_ERR = 8;//数据库删除失败
    public final static Integer DB_NO_RECORD = 9;//数据库中无此记录

    public final static Integer NO_FACE_INFO = 10;//未检测出人脸信息
    public final static Integer NO_EVEN_UPLOAD_ANY_PHOTO = 11;//用户还未上传过图片

    public final static Integer RABBIT_MSG_SEND_FAIL = 20;//发送消息失败

    public final static Integer NO_SUCH_PIC_SOURCE = 30;//还未上传对照参数
}
