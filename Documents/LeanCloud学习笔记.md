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
适用于用户并不关心具体保存到服务器的具体时间，或者数据并不需要时常与服务器发生交互时，可以使用本方法 在网络请求遇到异常时，AVOS Cloud会将此类请求保存到本地，等到网络回复正常或者排除故障以后再发送请求 被保存下来的请求会按照初始的发送顺序进行发送
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

1. ARRAY类型字段添加数据

		List<String> hobbies = new ArrayList<>();
	    hobbies.add("running");
	    hobbies.add("fly");
	    student.addAll("hobbies", hobbies);
	    student.save();

		......

		student.add("hobbies", "swimming");
    	student.save();



























