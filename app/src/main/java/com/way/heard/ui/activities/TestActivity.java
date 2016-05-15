package com.way.heard.ui.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.way.heard.R;
import com.way.heard.models.Post;

import java.util.List;

public class TestActivity extends AppCompatActivity {
    private final static String TAG = TestActivity.class.getName();
    private static final int IMAGE_PICK_REQUEST = 0;

    //    Button btnAdd;
    TextView tvMessage;
    String message = "";
    //    Button btnTest;
//    Button btnLeanUser;
//    Button btnFolder;
//    Button btnPick;
//    ImageView ivDisplay;
//    Button btnSelect;
//    RecyclerView rvPhotoList;
//
    private static Bitmap bitmap;

    Button btnQuery;
    private List<Post> posts;

    private Button btnAvatar;
    private ImageView ivAvatar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
//
//        initView1();
//
//        initView2();
    }

//    private void initView1() {
//        btnAvatar = (Button) findViewById(R.id.btn_test_avatar);
//        ivAvatar = (ImageView) findViewById(R.id.iv_test_avatar);
//        ivAvatar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                pickImage(TestActivity.this, IMAGE_PICK_REQUEST);
//            }
//        });
//        btnAvatar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new LeanCloudBackgroundTask(TestActivity.this) {
//
//                    @Override
//                    protected void onPre() {
//
//                    }
//
//                    @Override
//                    protected void doInBack() throws AVException {
//                        AVUser currentUser = AVUser.getCurrentUser();
//
//                        if (bitmap != null) {
//                            ByteArrayOutputStream out = new ByteArrayOutputStream();
//                            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
//                            byte[] data = out.toByteArray();
//                            AVFile avfile = new AVFile(currentUser.getUsername() + ".jpg", data);
//                            avfile.save();
//
//                            String url = avfile.getUrl();
//                            String thumbnailurl = avfile.getThumbnailUrl(true, 100, 100);
//
//                            Image img = new Image();
//                            img.setAuthor(currentUser);
//                            img.setThumbnailurl(thumbnailurl);
//                            img.setUrl(url);
//                            img.save();
//
//                            currentUser.put("avatar", thumbnailurl);
//                            currentUser.save();
//                        }
//                    }
//
//                    @Override
//                    protected void onPost(AVException e) {
//                        if (e == null) {
//                            Toast.makeText(TestActivity.this, "Done", Toast.LENGTH_SHORT).show();
//                        } else {
//                            Toast.makeText(TestActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }.execute();
//            }
//        });
//    }
//
//    private void initView2() {
//        tvMessage = (TextView) findViewById(R.id.tv_test_message);
//        btnQuery = (Button) findViewById(R.id.btn_test_query);
//        btnQuery.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                posts = null;
//                message = "";
//                new LeanCloudBackgroundTask(TestActivity.this) {
//
//                    @Override
//                    protected void onPre() {
//
//                    }
//
//                    @Override
//                    protected void doInBack() throws AVException {
//                        AVUser user = AVUser.getCurrentUser();
//                        posts = LeanCloudDataService.getAnyPublicPostsByUserByPage(user.getObjectId(),0, 15);
//                    }
//
//                    @Override
//                    protected void onPost(AVException e) {
//                        if (e == null) {
//                            try {
//                                if (posts != null && posts.size() > 0) {
//                                    for (Post post : posts) {
//                                        message += "Content = " + post.getContent() + " ; ";
//                                        message += "Tag = " + post.getTag() + " ; ";
//                                        message += "Author = " + post.getAuthor().getUsername() + " ; ";
//                                        message += "Type = " + post.getType() + " ; ";
//                                        List<Image> images = post.tryGetPhotoList();
//                                        if (images != null && images.size() > 0) {
//                                            message += "Photo key = " + post.tryGetPhotoList().get(0) + " ; ";
//                                        }
//                                        break;
//                                    }
//                                }
//                                tvMessage.setText(message);
//                            } catch (Exception e1) {
//                                Toast.makeText(TestActivity.this, "Exception, " + e1.getMessage(), Toast.LENGTH_SHORT).show();
//                                LogUtil.e(TAG, "onClick debug, onPost error", e1);
//                            }
//                        } else {
//                            Toast.makeText(TestActivity.this, "AVException, " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                            LogUtil.e(TAG, "onClick debug, onPost error", e);
//                        }
//                    }
//                }.execute();
//            }
//        });
//    }
//
////    private void initView1() {
////        tvMessage = (TextView) findViewById(R.id.tv_test_message);
////        btnAdd = (Button) findViewById(R.id.btn_test_add);
////        btnAdd.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////
////
////                new TestTask(TestActivity.this).execute();
////            }
////        });
////
////        btnTest = (Button) findViewById(R.id.btn_test_test);
////        btnTest.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                new LeanCloudBackgroundTask(TestActivity.this) {
////
////                    @Override
////                    protected void onPre() {
////
////                    }
////
////                    @Override
////                    protected void doInBack() throws AVException {
////                        AVUser currentUser = AVUser.getCurrentUser();
////                        String path = "/storage/emulated/0/Pictures/Screenshots/a.png";
////                        //
////                        String url = "";
////                        String thumbnailurl = "";
////                        try {
////                            if (bitmap != null) {
////                                ByteArrayOutputStream out = new ByteArrayOutputStream();
////                                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
////                                byte[] data = out.toByteArray();
////                                AVFile avfile = new AVFile("test.jpg", data);
////                                avfile.save();
////                                url = avfile.getUrl();
////                                thumbnailurl = avfile.getThumbnailUrl(true, 100, 100);
////                            }
////                            /*
////                            AVFile avfile = new AVFile("1625236515628731156.jpg", "http://img0.ph.126.net/GysjRL6GCIjTpOjhvtQX6Q==/1625236515628731156.jpg", new HashMap<String, Object>());
////                            avfile.save();
////                            url = avfile.getUrl();
////                            thumbnailurl = avfile.getThumbnailUrl(true, 100,100);
////                            */
////                        } catch (Exception e) {
////                            e.printStackTrace();
////                        }
////
////
////                        Image img = new Image();
////                        img.setAuthor(currentUser);
//////                        img.setThumbnailurl(file.getThumbnailUrl(true, 100, 100));
//////                        img.setUrl(file.getUrl());
////                        img.setThumbnailurl(thumbnailurl);
////                        img.setUrl(url);
////                        img.save();
////
////                        currentUser.put("avatar", img.getObjectId());
////                        currentUser.save();
////                    }
////
////                    @Override
////                    protected void onPost(AVException e) {
////                        if (e == null) {
////                            tvMessage.setText("Done");
////                            Toast.makeText(TestActivity.this, "Done", Toast.LENGTH_SHORT).show();
////                        } else {
////                            tvMessage.setText(e.toString());
////                            Toast.makeText(TestActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
////                        }
////                    }
////                }.execute();
////            }
////        });
////
////        btnLeanUser = (Button) findViewById(R.id.btn_test_leanuser);
////        btnLeanUser.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                final AVUser currentUser = AVUser.getCurrentUser();
////                final String path = "/storage/emulated/0/Pictures/Screenshots/a.png";
////
////                try {
////                    final AVFile file = AVFile.withAbsoluteLocalPath(currentUser.getUsername(), path);
////                    file.saveInBackground(new SaveCallback() {
////                        @Override
////                        public void done(AVException e) {
////                            if (null == e) {
////                                Image img = new Image();
////                                img.setAuthor(currentUser);
////                                img.setThumbnailurl(file.getThumbnailUrl(true, 100, 100));
////                                img.setUrl(file.getUrl());
////                                img.saveInBackground();
////                                currentUser.put("avatar", img.getObjectId());
////                                currentUser.saveInBackground();
////                            } else {
////                                tvMessage.setText(e.toString());
////                                Toast.makeText(TestActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
////                            }
////                        }
////                    });
////                } catch (IOException e) {
////                    e.printStackTrace();
////                    tvMessage.setText(e.toString());
////                    Toast.makeText(TestActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
////                }
////            }
////        });
////
////        btnFolder = (Button) findViewById(R.id.btn_test_folder);
////        btnFolder.setOnClickListener(new View.OnClickListener() {
////
////            @Override
////            public void onClick(View v) {
////                try {
////                    /*
////                    tvMessage.setText(Environment.getExternalStorageDirectory().getAbsolutePath());
////                    String path = "/storage/emulated/0/我的壁纸/a.jpg";
////                    File file = new File(path);
////                    if(file != null) {
////                        tvMessage.setText(file.getAbsolutePath());
////                        byte[] data = FileUtil.getBytesFromFile(file);
////                        int lengh = data.length;
////                        Toast.makeText(TestActivity.this, lengh, Toast.LENGTH_SHORT).show();
////                    }else {
////                        Toast.makeText(TestActivity.this, "No File", Toast.LENGTH_SHORT).show();
////                        String allFilePath = "";
////                        File root = Environment.getExternalStorageDirectory();
////                        File[] fileList = root.listFiles();
////                        for (File subFile : fileList){
////                            allFilePath += subFile.getName() + ";";
////                        }
////                        tvMessage.setText(allFilePath);
////                    }
////                    */
////                    if (bitmap != null) {
////                        ByteArrayOutputStream out = new ByteArrayOutputStream();
////                        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
////                        byte[] data = out.toByteArray();
////                        int lengh = data.length;
////                        Toast.makeText(TestActivity.this, "Bitmap Length" + lengh, Toast.LENGTH_SHORT).show();
////                    } else {
////                        Toast.makeText(TestActivity.this, "No Bitmap", Toast.LENGTH_SHORT).show();
////                    }
////                } catch (Exception e) {
////                    //tvMessage.setText(e.toString());
////                    Toast.makeText(TestActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
////                }
////            }
////        });
////
////        ivDisplay = (ImageView) findViewById(R.id.iv_test_display);
////        btnPick = (Button) findViewById(R.id.btn_test_pick);
////        btnPick.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                pickImage(TestActivity.this, IMAGE_PICK_REQUEST);
////            }
////        });
////
////
////        btnSelect = (Button) findViewById(R.id.btn_test_select);
////        rvPhotoList = (RecyclerView) findViewById(R.id.rv_test_list);
////        btnSelect.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                try {
////                    ImageConfig imageConfig
////                            = new ImageConfig.Builder(
////                            // GlideLoader 可用自己用的缓存库
////                            new GlideLoader())
////                            // 如果在 4.4 以上，则修改状态栏颜色 （默认黑色）
////                            .steepToolBarColor(getResources().getColor(R.color.blue))
////                            // 标题的背景颜色 （默认黑色）
////                            .titleBgColor(getResources().getColor(R.color.blue))
////                            // 提交按钮字体的颜色  （默认白色）
////                            .titleSubmitTextColor(getResources().getColor(R.color.white))
////                            // 标题颜色 （默认白色）
////                            .titleTextColor(getResources().getColor(R.color.white))
////                            // 开启多选   （默认为多选）  (单选 为 singleSelect)
////                            //.singleSelect()
////                            //.crop()
////                            // 多选时的最大数量   （默认 9 张）
////                            .mutiSelectMaxSize(9)
////                            // 已选择的图片路径
////                            .pathList(photopaths)
////                            // 拍照后存放的图片路径（默认 /temp/picture）
////                            .filePath("/Heard/Pictures")
////                            // 开启拍照功能 （默认开启）
////                            //.showCamera()
////                            .requestCode(REQUEST_CODE)
////                            .build();
////
////
////                    ImageSelector.open(TestActivity.this, imageConfig);   // 开启图片选择器
////                } catch (Exception e) {
////                    Toast.makeText(TestActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
////                }
////            }
////        });
////
////        GridLayoutManager gridLayoutManager = new GridLayoutManager(TestActivity.this, 3);
////        rvPhotoList.setLayoutManager(gridLayoutManager);
////        adapter = new PhotoViewAdapter(TestActivity.this, photopaths);
////        rvPhotoList.setAdapter(adapter);
////    }
////
////    public final static int REQUEST_CODE = 1000;
////    private PhotoViewAdapter adapter;
////    private ArrayList<String> photopaths = new ArrayList<>();
////
////    class TestTask extends LeanCloudBackgroundTask {
////
////
////        protected TestTask(Context ctx) {
////            super(ctx);
////        }
////
////        @Override
////        protected void onPre() {
////
////        }
////
////        @Override
////        protected void doInBack() throws AVException {
////            AVFile avFile = null;
////            try {
////                avFile = AVFile.withAbsoluteLocalPath("a.png", "/storage/emulated/0/Pictures/Screenshots/a.png");
////            } catch (FileNotFoundException e) {
////                e.printStackTrace();
////            }
////            avFile.save();
////            //LeanUser currentUser = LeanUser.getCurrentUser();
////            //currentUser.put("avatar",avFile);
////            //currentUser.save();
////        }
////
////        @Override
////        protected void onPost(AVException e) {
////            if (e == null) {
////                tvMessage.setText("Successful");
////                Toast.makeText(TestActivity.this, "AVFile Successful", Toast.LENGTH_LONG).show();
////            } else {
////                tvMessage.setText("Failed" + e.getMessage());
////                Toast.makeText(TestActivity.this, "AVFile Failed", Toast.LENGTH_LONG).show();
////            }
////        }
////    }
//
//    public static void pickImage(Activity activity, int requestCode) {
//        Intent intent = new Intent(Intent.ACTION_PICK, null);
//        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
//        activity.startActivityForResult(intent, requestCode);
//    }


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == Activity.RESULT_OK) {
//            if (requestCode == IMAGE_PICK_REQUEST) {
//                Uri uri = data.getData();
//                try {
//                    tvMessage.setText(uri.getPath());
//                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
//                    ivAvatar.setImageBitmap(bitmap);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
}