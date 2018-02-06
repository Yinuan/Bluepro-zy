# Bluepro-zy
    使用流程：
        项目工程目录
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
         以及app工程下
  	dependencies {
	        compile 'com.github.Yinuan:Bluepro-zy:v1.2'
	}

          调用方式：
     用户端调用如下：
               Intent intent =new Intent();
                intent.setClass(MainActivity.this, MainUserActivity.class);
                intent.putExtra("tellPhone","0078");
                intent.putExtra("PrjID","0");
                startActivity(intent);
     管理端调用方式：
               Intent intent =new Intent();
                intent.setClass(MainActivity.this, MainAdminActivity.class);
                intent.putExtra("tellPhone","18565651403");
                intent.putExtra("PrjID","0");
                startActivity(intent);
          另外清单文件，需要添加的额外。
   Eclispe  

