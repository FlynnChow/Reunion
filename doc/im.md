### 请求用户所有未读消息

```
路径
obtain/imMessages

请求参数
uid String 用户uid

响应结果
{
  code 状态->200：成功且有数据 300:没有未读消息 400:异常
  msg  消息->异常原因
  data[]{ 
          imId:消息的id
          messages[]<MessageBean> 消息bean ，按照time 降序排序
        }
}
``` 
