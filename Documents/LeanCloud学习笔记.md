1. 获取第一行

		AVQuery<Studentq = AVObject.getQuery(Student.class);
		Student student = q.getFirst();

2. 根据已有ObjectID获取数据

		Student student = AVObject.createWithoutData(Student.class, "objectId");
    	AVObject fetched = student.fetch();

3. 保存文件

		AVFile avatar = new AVFile("avatar", getAvatarBytes());
    	Student student = new Student();
    	student.setName("name");
    	student.setAvatar(avatar);
    	student.save();

4. 序列化

		//将对象序列化成字符串
		Student student = getFirstStudent();
    	String s = student.toString();
    
		//从字符串中解析对象
    	AVObject parseObject = AVObject.parseAVObject(s);

1. AVObject已经实现了Parcelable接口

		public class AVObject implements Parcelable {
		......
		}

		......

		Intent intent = new Intent();
    	intent.putExtra("student", student);

    	Student intentStudent = intent.getParcelableExtra("student");

1. 离线保存对象

		public void saveEventually(SaveCallback callback) {
		......
		}
		
		......

		Student student = new Student();
    	student.setName("testOfflineSave");
    	student.saveEventually();

    >   适用于用户并不关心具体保存到服务器的具体时间，或者数据并不需要时常与服务器发生交互时，可以使用本方法 在网络请求遇到异常时，AVOS Cloud会将此类请求保存到本地，等到网络回复正常或者排除故障以后再发送请求 被保存下来的请求会按照初始的发送顺序进行发送
由于保存的时间无法确定，回调发生时可能已经超出了原来的运行环境，即便发生也没有意义，所以不鼓励用户saveEventually中传入callback

1. 整形字段的自增方法

		student.increment("age", 1);
    	student.save();
		
1. any字段可以存储任意类型
		
		//Any 字段保存为了数字
		student.setAny(1);
    	student.save();

		//Any 字段保存为了字符串
    	student.setAny("hello");
    	student.save();

		//Any 字段保存为了Map
    	HashMap<String, Object> map = new HashMap<>();
    	map.put("like", "swimming");
    	student.setAny(map);
    	student.save();

		student.getAny()

1. 讲某个字段所有数据置空

		student.remove("age");
    	student.save();

1. ARRAY类型字段添加或移除数据

		List<String> hobbies = new ArrayList<>();
	    hobbies.add("running");
	    hobbies.add("fly");
	    student.addAll("hobbies", hobbies);
	    student.save();

		......

		student.add("hobbies", "swimming");
    	student.save();

        ......
        
        List<String> removeHobbies = new ArrayList<>();
        removeHobbies.add("swimming");
        student.removeAll("hobbies", removeHobbies);
        student.save();

        ......

        //添加唯一数据，存在就覆盖，保证唯一性
        student.addUnique("hobbies", "swimming");
        student.save();

1. 添加多条数据，批量保存

        List<Student> students = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Student student = new Student();
            student.setName(i + "");
            student.setAge(i + 10);
            students.add(student);
        }
        AVObject.saveAll(students);

        ......

        List<Student> students = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                Student student = new Student();
                student.setName(i + "");
                AVFile avatar = new AVFile("avatar" + i, getAvatarBytes());
                student.setAvatar(avatar);
                students.add(student);
            }
            AVObject.saveAll(students);

1. 修改多条数据，批量修改

        for (Student student : students) {
            student.setName("testBatchUpdate");
        }
        AVObject.saveAll(students);

1. 删除多条数据，批量删除

        AVObject.deleteAll(students);

1. 从ARRAY类型字段获取数据

        AVObject myObject = new AVObject("Student");//新建AVObject对象会自动生成ObjectID
        for (int i = 0; i < 5; ++i) {
            myObject.add("array", i);
        }
        myObject.save();
        
        AVQuery<AVObject> query = AVQuery.getQuery("Student");
        AVObject result = query.get(myObject.getObjectId());
        List<Number> array = result.getList("array");

1. 查询全部数据

        AVQuery<Student> query = AVQuery.getQuery(Student.class);
        List<Student> students = query.find();

1. 获取第一条记录

        Student student = query.getFirst();

1. 限制记录行数

        query.limit(2);
        List<Student> students = query.find();

1. 递减，降序

        query.orderByDescending("createdAt");
        query.skip(3);
        Student first = query.getFirst();

1. 递增，升序

        query.orderByAscending("createdAt").limit(5);

1. 条件查询，AND查询

        query.whereNotEqualTo(Student.NAME, "Mike");
        // 默认就是 And
        query.whereStartsWith(Student.NAME, "M");

1. 条件查询，OR查询

        query1.whereEqualTo(Student.NAME, "Mike");
        query2.whereStartsWith(Student.NAME, "J");

        List<AVQuery<Student>> queries = new ArrayList<>();
        queries.add(query1);
        queries.add(query2);

        AVQuery<Student> query = AVQuery.or(queries);
        List<Student> students = query.find();

1. 二次排序

        query.orderByDescending(Student.NAME)
                .addDescendingOrder(Student.AGE)
                .limit(5);
        List<Student> students = query.find();

1. ARRAY类型字段的查询匹配

        query.whereSizeEqual("hobbies", 2).limit(10);

1. 某个字段的多条件匹配

        //字符匹配
        query.whereStartsWith("name", "M")
        .whereEndsWith("name", "e")
        .whereContains("name", "i");
        
        ......
        
        //包含其中之一
        query.whereContainedIn("name", Arrays.asList("Mike", "Jane"));
        List<Student> students = query.find();

        ......
        
        //全部包含
        query.whereContainsAll("hobbies", Arrays.asList("swimming", "running"));
        List<Student> students = query.find();

1. 通过正则表达式查询匹配

        query.whereMatches("name", "^M.*");

1. 开启全局省流量模式

		// 应该放在 Application 的 onCreate 中，开启全局省流量模式
	    AVOSCloud.setLastModifyEnabled(true);
	
	    Student student = getFirstStudent();
	
	    // 此处服务器应该返回了所有数据
	    AVQuery<Student> q = AVQuery.getQuery(Student.class);
	    Student student1 = q.get(student.getObjectId());
	    log("从服务器获取了对象：" + prettyJSON(student1));
	
	    // 客户端把该对象的udpatedAt传给服务器，服务器判断对象未改变，于是返回 304 和空数据，客户端返回本地缓存的数据，节省流量
	    Student student2 = q.get(student.getObjectId());

		......

		q.limit(5);
	    // 此处服务器应该返回了所有数据
	    List<Student> students = q.find();

		// 服务器记录表的修改时间，如果两次查询之间表未被修改且参数一样，则以下查询将从本地缓存获取数据
	    List<Student> students1 = q.find();

1. 设置查询策略，本地缓存或网络

		AVQuery<Student> q = AVQuery.getQuery(Student.class);

		//1.CACHE_THEN_NETWORK

		//查询首先尝试从缓存中获取，然后再从网络获取。在这种情况下，FindCallback 会被实际调用两次：
		//首先是缓存的结果，其次是网络查询的结果。这个缓存策略只能用在异步的 findInBackground 方法中。
	    q.setCachePolicy(AVQuery.CachePolicy.CACHE_THEN_NETWORK);
	    // 单位毫秒
	    q.setMaxCacheAge(1000 * 60 * 60); // 一小时
	    q.limit(1);
	    q.findInBackground(new FindCallback<Student>() {
	      int count = 0;
	
	      @Override
	      public void done(List<Student> list, AVException e) {
	      	if (count == 0) {
	        	log("第一次从缓存中获取了结果：" + prettyJSON(list));
	        } else {
	        	log("第二次从网络获取了结果：" + prettyJSON(list));
	        }
	        count++;
	      }
	    });


		......

		//2.CACHE_ELSE_NETWORK

		//查询首先尝试从缓存中获取，如果失败，则从网络获取，如果两者都失败，则引发一个 AVException。
		q.setCachePolicy(AVQuery.CachePolicy.CACHE_ELSE_NETWORK);
	    // 单位毫秒
	    q.setMaxCacheAge(1000 * 60 * 60); // 一小时
	    q.limit(1);
	    if (q.hasCachedResult()) {
	      log("有本地缓存，将从本地获取");
	    } else {
	      log("无本地缓存，将从服务器获取");
	    }
	    List<Student> students = q.find();

		......

		//3.NETWORK_ELSE_CACHE

		//查询首先尝试从网络获取，如果失败，则从缓存中查找；如果两者都失败，则应发一个 AVException。
		q.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
	    // 单位毫秒
	    q.setMaxCacheAge(1000 * 60 * 60); // 一小时
	    q.limit(1);
	    if (q.hasCachedResult()) {
	      log("有本地缓存，无网络时将从本地获取");
	    } else {
	      log("无本地缓存，将从服务器获取");
	    }

		......

		//4.NETWORK_ONLY

		//查询不走缓存，从网路中获取，但是查询结果会写入缓存。
		q.setCachePolicy(AVQuery.CachePolicy.NETWORK_ONLY);
	    // 单位毫秒
	    q.setMaxCacheAge(1000 * 60 * 60); // 一小时
	    q.limit(1);
	    if (q.hasCachedResult()) {
	      log("有本地缓存，但无视之");
	    } else {
	      log("无本地缓存，也无视之");
	    }

		......

		//5.CACHE_ONLY

		//查询只从缓存获取，不走网络。如果缓存中没有结果，引发一个 AVException。
		q.setCachePolicy(AVQuery.CachePolicy.CACHE_ONLY);
	    // 单位毫秒
	    q.setMaxCacheAge(1000 * 60 * 60); // 一小时
	    q.limit(1);
	    if (q.hasCachedResult()) {
	      log("有本地缓存，将从本地获取结果");
	    } else {
	      log("无本地缓存，将抛出异常，请先运行上一个例子，从网络获取结果保存到本地");
	    }

		......

		//6.IGNORE_CACHE

		//默认的缓存策略，查询不走缓存，查询结果也不存储在缓存。
		log("此策略才网络获取结果，并不保存结果到本地");
	    q.setCachePolicy(AVQuery.CachePolicy.IGNORE_CACHE);
	    // 单位毫秒
	    q.setMaxCacheAge(1000 * 60 * 60); // 一小时
	    q.limit(1);

1. 清除缓存

		//清除当前查询缓存
		AVQuery<Student> q = AVQuery.getQuery(Student.class);
	    q.limit(1);
	    q.clearCachedResult();

		......

		//清除所有缓存
		AVQuery.clearAllCachedResults();
	    log("已删除所有的缓存");

1. whereMatchesKeyInQuery的使用，相应的有whereDoesNotMatchKeyInQuery

		//Add a constraint to the query that requires a particular key's value matches a value for a key in the results
		//of another AVQuery
		//当前查询的某个字段与另外一个查询的某个字段的值相匹配
		AVQuery q1 = AVQuery.getQuery("Person");
	    
	    AVQuery q2 = AVQuery.getQuery("Something");
	    q2.whereMatchesKeyInQuery("belongTo", "name", q1);

1. 注册用户

		AVUser user = new AVUser();
	    user.setUsername("waylanpunch");
	    user.setPassword("111111");
	    user.signUp();
	    Assert.assertFalse(user.getObjectId().isEmpty());
	    String username = user.getUsername();

1. 登录

		AVUser.logOut();
        AVUser.logInInBackground(username, password, new LogInCallback<AVUser>() {
          	@Override
          	public void done(AVUser avUser, AVException e) {
            	if (e != null) {
              		log(e.getMessage());
            	} else {
              		log("登录成功 user：" + avUser.toString());
            	}
        	}
        });

    
1. 当前已登录的用户

		AVUser user = AVUser.getCurrentUser();

1. 注销当前已登录的用户

		AVUser.logOut();

1. 删除当前已登录的用户

		user.delete();
      	log("已删除当前用户");

1. 修改用户数据

		AVQuery<AVUser> q = AVUser.getQuery();
	    AVUser first = q.getFirst();
	    log("获取了一个用户，但未登录该用户");
	    first.put("city", "ShangHai");
	    try {
	      first.save();
	    } catch (AVException e) {
	      if (e.getCode() == AVException.SESSION_MISSING) {
	        log("尝试修改未登录用户的数据，发生错误：" + e.getMessage());
	      } else {
	        throw e;
	      }
	    }
	    log("结论：不能修改未登录用户的数据");

		......

		//修改密码
		user.updatePassword(oldPassword, newPassword);

1. 验证注册用户手机号码

		// 请在网站勾选 "验证注册用户手机号码" 选项，否则不会发送验证短信
		final AVUser user = new AVUser();
        user.setUsername("test");
        user.setPassword("test");
        user.setMobilePhoneNumber("15866778899");
        user.signUpInBackground(new SignUpCallback() {
          	@Override
          	public void done(AVException e) {
            	if (e == null) {
                  	AVUser.verifyMobilePhoneInBackground("verifycode", new AVMobilePhoneVerifyCallback() {
                    	@Override
                    	public void done(AVException e) {
                      		if (e == null) {
                        		log("注册成功, user:" + user);
                      		}
                   		}
                	});
            	}
          	}
        });

1. 输入手机号码+密码登录
      
		AVUser.loginByMobilePhoneNumberInBackground("15866778899", demoPassword, new LogInCallback<AVUser>() {
	          @Override
	          public void done(AVUser avUser, AVException e) {
	            if (e == null) {
	              log("登录成功, user:" + avUser);
	            }
	          }
	    });

1. 输入手机号码+验证码登录
      
		AVUser.requestLoginSmsCodeInBackground("15866778899", new RequestMobileCodeCallback() {
          @Override
          public void done(AVException e) {
            if (e == null) {
                  AVUser.loginBySMSCodeInBackground("15866778899", smsCode, new LogInCallback<AVUser>() {
                    @Override
                    public void done(AVUser avUser, AVException e) {
                      if (e == null) {
                        log("登录成功, user: " + avUser);
                      }
                    }
                  });
            }
          }
        });

1. 使用手机号码重置密码

		AVUser.requestPasswordResetBySmsCodeInBackground("15866778899", new RequestMobileCodeCallback() {
          @Override
          public void done(AVException e) {
            if (e == null) {
                  final String newPassword = "abcdefg";
                  AVUser.resetPasswordBySmsCodeInBackground(smsCode, newPassword, new UpdatePasswordCallback() {
                    @Override
                    public void done(AVException e) {
                      if (e == null) {
                        log("密码更改成功，新密码 " + newPassword);
                        log("试着用手机号和新密码登录吧");
                      }
                    }
                  });
            }
          }
        });

1. 邮箱验证

		//请确认控制台已开启注册时开启邮箱验证，这样才能收到验证邮件
		final AVUser user = new AVUser();
        user.setUsername(text);
        user.setPassword(demoPassword);
        user.setEmail(text);
        user.signUpInBackground(new SignUpCallback() {
          @Override
          public void done(AVException e) {
            if (e == null) {
              log("注册成功，user: " + user);
            }
          }
        });

1. 邮箱登录

		AVUser.logInInBackground("waylanpunch@gmail.com", "testpassword", new LogInCallback<AVUser>() {
          @Override
          public void done(AVUser avUser, AVException e) {
            if (e == null) {
              log("登录成功 user:" + avUser);
            }
          }
        });

1. 使用邮箱进行密码重置

		AVUser.requestPasswordResetInBackground(text, new RequestPasswordResetCallback() {
          @Override
          public void done(AVException e) {
            if (e == null) {
              log("重置密码的邮件已发送到邮箱 " + text);
            }
          }
        });

1. 匿名登录

		AVAnonymousUtils.logIn(new LogInCallback<AVUser>() {
      		@Override
      		public void done(AVUser avUser, AVException e) {
        		if (e == null) {
          			log("创建了一个匿名用户并登录，user:" + avUser);
        		}
      		}
    	});

1. 文件上传

		//文件转换成字节数组
		byte[] data = DemoUtils.readFile(file);
        final AVFile avFile = new AVFile(file.getName(), data);
        avFile.saveInBackground(new SaveCallback() {
          @Override
          public void done(AVException e) {
            if (e == null) {
              fileUrl = avFile.getUrl();
              objectId = avFile.getObjectId();
              log("文件上传成功 url:" + fileUrl);
            } else {
              log(e.getMessage());
            }
          }
        }, new ProgressCallback() {
          @Override
          public void done(Integer percentDone) {
            log("uploading: " + percentDone);
          }
        });

1. 文件下载

		AVFile avFile = new AVFile("my_download_file", fileUrl, null);
	    byte[] bytes = avFile.getData();
	    log("下载文件完毕，总字节数：" + bytes.length);
	
1. 文件删除	
	
		// 需要控制台开启权限
		AVFile avFile = AVFile.withObjectId(objectId);
	    avFile.delete();

1. 根据文件绝对路径上传

		File tmpFile = ......
	    AVFile file = AVFile.withAbsoluteLocalPath("filename", tmpFile.getAbsolutePath());
	    file.save();
	    log("从文件的路径中构造了 AVFile，并保存成功。file:" + toString(file));

1. 直接上传文件

		File tmpFile = ......
	
	    AVFile file = AVFile.withFile("filename", tmpFile);
	    file.save();
	    log("用文件构造了 AVFile，并保存成功。file:" + toString(file));

1. AVObject转换成AVFile

		AVQuery<AVObject> q = new AVQuery<>("_File");
    	AVObject first = q.getFirst();
    	log("获取了文件 AVObject：" + first);

    	AVFile file = AVFile.withAVObject(first);

		......

		//根据ObjectId转换
		AVFile file = AVFile.withObjectId(first.getObjectId());

1. 修改AVFile元数据

		Bitmap bitmap = ......
		bytes = ......bitmap......

		AVFile file = new AVFile("filename", bytes);
	    file.addMetaData("width", bitmap.getWidth());
	    file.addMetaData("height", bitmap.getHeight());
	    file.save();

1. AVFile获取图片缩略图

		AVFile avatar = ......
	    String url = avatar.getThumbnailUrl(true, 200, 200);
	    log("最大宽度为200 、最大高度为200的缩略图 url:" + url);
	    //[http://docs.qiniu.com/api/v6/image-process.html](http://docs.qiniu.com/api/v6/image-process.html "七牛文档")
	    log("其它图片处理见七牛文档");

1. POINTER类型存储另外一个对象一个实例的ObjectId

        AVObject post = new AVObject("Post");
        post.put("author", user);
        post.save();
        
1. ARRAY类型存储另外一个对象多个实例的ObjectId

        AVObject post = new AVObject("Post");
        post.addAll("likes", users);
        post.save();

    ：注意add与put的区别，一个是在原ARRAY数组末尾新增，一个是直接赋值
    
        add(java.lang.String key, java.lang.Object value)
        //Atomically adds an object to the end of the array associated with a given key.
        
        put(java.lang.String key, java.lang.Object value)
        //Add a key-value pair to this object.

    ：ARRAY可以存储多个POINTER类型
    
1. 联表查询
        
        //返回的数据将只包含ClassName和ObjectId
        AVQuery<Post> query = AVQuery.getQuery("Post");
        query.whereExists("likes");
        log("将不包含 likes 字段的具体数据");
        Post first = query.getFirst();

        ......
        
        //返回的数据除了ClassName和ObjectId字段，还包含其他所有字段
        query.whereExists(Post.LIKES);
        log("让返回结果包含了 likes 字段的具体数据，不单单是赞的人的 objectId");
        query.include(Post.LIKES);
        Post first = query.getFirst();
        
    ：注意
    
        POINTER ： 一对一的关系
        ARRAY ： 一对多的关系
        AVRelation ：多对多的关系 

1. AVRelation类型的使用，添加数据

        Post post = query1.getFirst();
        Student student = query2.getFirst();
    
        AVRelation<Student> rewardStudents = post.getRelation("rewards");
        rewardStudents.add(student);
        post.save();

1. AVRelation类型的使用，移除数据

        rewardStudents.remove(student);
        post.save();

1. AVRelation类型的使用，查询数据

        AVRelation<Student> students = post.getRelation("rewards");
        AVQuery<Student> query = students.getQuery();
        List<Student> stuList = query.find();

        ......
        
        int count = query.count();

        ......
        
        AVQuery<Post> postQuery = AVRelation.reverseQuery(Post.class, "rewards", student);
        List<Post> posts = postQuery.find();

        ：注意，reverseQuery(java.lang.Class<M> theParentClazz, java.lang.String relationKey, AVObject child)
        Create a query that can be used to query the parent objects in this relation.

1. 类的继承，创建一个类SubUser继承自AVUser

    	public class SubUser extends AVUser {
        	public static final Creator CREATOR = AVObjectCreator.instance;
			......
    	}

	：注意，SubUser继承自AVUser类
    
        Armor armor = new Armor();
        armor.setDisplayName("avos cloud demo object.");
        armor.setBroken(false);
        armor.save();

        SubUser subUser = new SubUser();
        String nickName = "testSignupSubUser";
        subUser.setUsername(username);
        subUser.setPassword(password);
        subUser.setNickName(nickName);
        subUser.setArmor(armor);
        subUser.signUp();
        
        SubUser cloudUser = AVUser.logIn(username, password, SubUser.class);
        AVUser currentUser = AVUser.getCurrentUser();

		......

		//可以利用创建的子类SubUser登录，因为SubUser继承了AVUser所有属性和方法
		SubUser.logInInBackground(username, password, new LogInCallback<AVUser>() {
	          @Override
	          public void done(AVUser avUser, AVException e) {
		            AVUser currentUser = AVUser.getCurrentUser();
		            Assert.assertTrue(currentUser instanceof SubUser);//Error
	          }
        });

	：注意，Armor继承自AVObject类。

		public class Armor extends AVObject {
  			public static final Creator CREATOR = AVObjectCreator.instance;
			......
		}

1. 使用SQL语句查询

	//1. select

		String cql = "select * from _User";
    	AVCloudQueryResult result = AVQuery.doCloudQuery(cql);

	//AVCloudQueryResult以Json对象展示

		result:[{
		        "username": "p1nrxk2393jpi8twib0f0wwpu",
			        "authData": {
			        	"anonymous": {
			        		"id": "nmey8BRBOHXJLNQnjWw0LqB"
			             }
			     	},
		         "emailVerified": false,
		         "className": "_User",
		         "mobilePhoneVerified": false,
		         "updatedAt": "2016-04-05T05:28:29.189Z",
		         "createdAt": "2016-04-05T05:28:29.189Z",
		         "objectId": "57034cfd71cfe4005cd64e05"
	         },{
                 "emailVerified": false,
                 "className": "_User",
                 "mobilePhoneVerified": false,
                 "username": "XiaoMing",
                 "updatedAt": "2016-04-05T05:28:45.473Z",
                 "createdAt": "2016-04-05T05:28:45.473Z",
                 "objectId": "57034d0d5bbb50004dc9f001"
            },
			......
			}]

		......

	//2. count

		String cql = "select count(*) from _User";
    	AVCloudQueryResult result = AVQuery.doCloudQuery(cql);

		{"results":[],"className":"_User","count":19}

		......

	//3. where

		String cql = String.format("select * from _User where username in (?,?)");
    	AVCloudQueryResult result = AVQuery.doCloudQuery(cql, "test1", "test2");

		......

	//4. order by

		String cql = String.format("select * from _User where createdAt < date(?) order by createdAt limit ?");
    	AVCloudQueryResult result = AVQuery.doCloudQuery(cql, "2016-05-01T00:00:00.0000Z", 3);

	//AVCloudQueryResult以Json对象展示

		result:[{
			"username": "p1nrxk2393jpi8twib0f0wwpu",
			"authData": {
				"anonymous": {
					"id": "nmey8BRBOHXJLNQnjWw0LqB"
				}
			},
			"emailVerified": false,
			"className": "_User",
			"mobilePhoneVerified": false,
			"updatedAt": "2016-04-05T05:28:29.189Z",
			"createdAt": "2016-04-05T05:28:29.189Z",
			"objectId": "57034cfd71cfe4005cd64e05"
			},{
			"emailVerified": false,
			"className": "_User",
			"mobilePhoneVerified": false,
			"username": "XiaoMing",
			"updatedAt": "2016-04-05T05:28:45.473Z",
			"createdAt": "2016-04-05T05:28:45.473Z",
			"objectId": "57034d0d5bbb50004dc9f001"
			},{
			"username": "9lsrmw91febck0fduf81o9fq9",
			"authData": {
				"anonymous": {
					"id": "zwAzy6L4MGVgsIhbmQLWMXB"
				}
			},
			"emailVerified": false,
			"className": "_User",
			"mobilePhoneVerified": false,
			"updatedAt": "2016-04-06T01:01:59.100Z",
			"createdAt": "2016-04-06T01:01:53.809Z",
			"objectId": "57046001128fe100524f097d"
			},]

1. 获取服务器时间

		Date date = AVOSCloud.getServerDate();

1. 设置网络响应时长

		// 得放到 Application 里
    	AVOSCloud.setNetworkTimeout(10);







































































