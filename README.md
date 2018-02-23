# YiKeApp

update 2018.01.16-9：35.  

新增了首页activity（HomeActivity）. 	

使用 ViewPager + TabLayout 实现（只用了一个fragment先进行测试，对应布局文件为 fragment_home_item_list）. 	

fragment只包含了简单的一个recycleview。（对应adapter 为 MyItemRecyclerViewAdapter，item布局文件为 fragment_home_item）. 	

item的内容格式用的是新写的虚拟项目类-HomeContent。 



新增了登录activity（LoginActivity）

将常量（如网络url）写进了ConstantValues类中，以后常量也放在这个类当中

网络请求中存在该接口文档所指向的地址证书不受认证的问题，需要修复。


update 2018.1.26-20：54.

修复了关于域名证书的bug

在HttpUtils的工具类当中重写了一个名为getUnsafeOkHttpClient()的静态方法，由于后台的域名是开启https，故访问时需要先定义一个OkHttpCient对象（例：OkHttpClient client = null;
try {
  client = HttpUtils.getUnsafeOkHttpClient();
  } catch (Exception e) {
    throw new RuntimeException(e);
  }
）

由于后台返回的是json数据，需要定义一个bean类去保存，暂未完成。

update 2018.1.29-22:38.

新增了一个填写资料的活动DetailActivity，但布局还未写好。

需要注意：以后在跳转活动中需要将intent绑定一个token，这个token自成功登录后就会有值（详见接口文档）。

update 2018.2.2-20:26.

将DetailActivity的布局完善了，需要注意的是发送验证码的接口只有40条免费短信，不要随便用，其它接口可随意测试。
 
update 
完善了 首页和消息的布局，还没设置点击事件，等待后台接口文档完成。
 
