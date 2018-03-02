# Bluepro-zy

正元对接无线蓝牙项目定制版使用流程：

     项目工程目录
	
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
         以及app工程下
  		dependencies {
	        compile 'com.github.Yinuan:Bluepro-zy:v1.3'
	}

  调用方式：
    
     findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent();
                intent.setClass(MainActivity.this, MainUserActivity.class);
                intent.putExtra("tellPhone","18565651403");
                intent.putExtra("PrjID","0");
                intent.putExtra("prijName","我的宿舍");
                intent.putExtra("app_url","http://106.75.164.143:8085/appI/api/");
                startActivity(intent);
            }
        });
        //操作员
        findViewById(R.id.button5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent();
                intent.setClass(MainActivity.this, MainAdminActivity.class);
                intent.putExtra("tellPhone","18565651403");
                intent.putExtra("PrjID","0");
                intent.putExtra("prijName","蓝牙项目");
                intent.putExtra("app_url","http://106.75.164.143:8085/appI/api/");
                startActivity(intent);
            }
        });
   另外清单文件，需要添加的额外。
   Eclispe  
   	![](https://github.com/Yinuan/Bluepro-zy/blob/master/mylibrary/src/main/res/mipmap-hdpi/go_home.png)

