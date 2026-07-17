package com.sunlee.sys.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: sunlee
 * @Date: 2026/01/15 21:35
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultObj {

    private Integer code;
    private String msg;

    public static final ResultObj LOGIN_SUCCESS=new ResultObj(Constast.OK,"登陆成功");
    public static final ResultObj LOGIN_ERROR_PASS=new ResultObj(Constast.ERROR,"用户名或密码错误");
    public static final ResultObj LOGIN_ERROR_CODE=new ResultObj(Constast.ERROR,"验证码错误");

    public static final ResultObj ADD_SUCCESS = new ResultObj(Constast.OK,"添加成功");
    public static final ResultObj ADD_ERROR = new ResultObj(Constast.ERROR,"添加失败");

    public static final ResultObj DELETE_SUCCESS = new ResultObj(Constast.OK,"删除成功");
    public static final ResultObj DELETE_ERROR = new ResultObj(Constast.ERROR,"删除失败");

    public static final ResultObj UPDATE_SUCCESS = new ResultObj(Constast.OK,"修改成功");
    public static final ResultObj UPDATE_ERROR = new ResultObj(Constast.ERROR,"修改失败");

    public static final ResultObj RESET_SUCCESS = new ResultObj(Constast.OK,"重置成功");
    public static final ResultObj RESET_ERROR = new ResultObj(Constast.ERROR,"重置失败");

    public static final ResultObj DISPATCH_SUCCESS = new ResultObj(Constast.OK,"分配成功");
    public static final ResultObj DISPATCH_ERROR = new ResultObj(Constast.ERROR,"分配失败");

    public static final ResultObj BACKINPORT_SUCCESS = new ResultObj(Constast.OK,"退货成功");
    public static final ResultObj BACKINPORT_ERROR = new ResultObj(Constast.ERROR,"退货失败");

    public static final ResultObj CANCEL_SUCCESS = new ResultObj(Constast.OK,"取消成功");
    public static final ResultObj CANCEL_ERROR = new ResultObj(Constast.ERROR,"取消失败");

    public static final ResultObj DELETE_ERROR_NEWS = new ResultObj(Constast.ERROR,"删除用户失败，该用户是其他用户的直属领导，请先修改该用户的下属的直属领导，再进行删除操作");
    public static final ResultObj DELETE_QUERY = new ResultObj();

    /**
     * 创建自定义错误结果
     * @param msg 错误消息
     * @return ResultObj
     */
    public static ResultObj error(String msg) {
        return new ResultObj(Constast.ERROR, msg);
    }

    /**
     * 创建自定义成功结果
     * @param msg 成功消息
     * @return ResultObj
     */
    public static ResultObj ok(String msg) {
        return new ResultObj(Constast.OK, msg);
    }

}
