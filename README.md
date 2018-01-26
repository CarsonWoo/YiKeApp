# YiKeApp

update 2018.01.16-9：35.  

新增了首页activity（HomeActivity）. 	

使用 ViewPager + TabLayout 实现（只用了一个fragment先进行测试，对应布局文件为 fragment_home_item_list）. 	

fragment只包含了简单的一个recycleview。（对应adapter 为 MyItemRecyclerViewAdapter，item布局文件为 fragment_home_item）. 	

item的内容格式用的是新写的虚拟项目类-HomeContent。 



新增了登录activity（LoginActivity）

将常量（如网络url）写进了ConstantValues类中，以后常量也放在这个类当中

网络请求中存在该接口文档所指向的地址证书不受认证的问题，需要修复。
