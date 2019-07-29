# Spring源码解析

Java中用Class来描述类对象，Spring中用beanDefinition (接口)来描述bean	

register()方法可以注册一个config.class也可以注册单个的indexDao.class,不过得调用refresh()，否则报错;

