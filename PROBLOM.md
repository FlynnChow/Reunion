# 记录一下开发中遇到的问题
1. viewPager中嵌套viewPager，内部的viewPager需要使用getChildFragmentManager()
来管理fragment，否则会内容空白，
