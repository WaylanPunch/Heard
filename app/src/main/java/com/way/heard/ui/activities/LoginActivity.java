package com.way.heard.ui.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.avos.avoscloud.AVUser;
import com.way.heard.R;
import com.way.heard.utils.InternetUtil;
import com.way.heard.utils.LeanCloudHelper;
import com.way.heard.utils.LogUtil;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {
    private final static String TAG = LoginActivity.class.getName();

    private static final int SIGNUPEVENT = 0;
    private static final int SIGNINEVENT = 1;

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    //private AutoCompleteTextView mEmailView;
    private AutoCompleteTextView mUsernameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        //mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mUsernameView = (AutoCompleteTextView) findViewById(R.id.username);

        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin(SIGNINEVENT);
                    return true;
                }
                return false;
            }
        });

        //Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        //mEmailSignInButton.setOnClickListener(new OnClickListener() {
        //@Override
        //public void onClick(View view) {
        //attemptLogin();
        //}
        //});

//        SegmentedGroup segmentedButton = (SegmentedGroup) findViewById(R.id.sg_login_button);
//        segmentedButton.setTintColor(getResources().getColor(R.color.colorAccent));
//        segmentedButton.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                switch (checkedId) {
//                    case R.id.rb_login_signup:
//                        attemptLogin(SIGNUPEVENT);
//                        break;
//                    default:
//                        attemptLogin(SIGNINEVENT);
//                        break;
//                }
//            }
//        });
        TextView tv_signup = (TextView) findViewById(R.id.tv_login_sign_up);
        TextView tv_signin = (TextView) findViewById(R.id.tv_login_sign_in);
        tv_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin(SIGNUPEVENT);//Sign up
            }
        });
        tv_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin(SIGNINEVENT);//Sign in
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        AVUser currentUser = AVUser.getCurrentUser();
        if (currentUser != null) {
            startMainActivity();
        }
    }

    public void startMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }
        //getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            //Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
            Snackbar.make(mUsernameView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin(int buttonEvent) {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        //mEmailView.setError(null);
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        //String email = mEmailView.getText().toString();
        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid username.
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        } else if (!isUsernameValid(username)) {
            mUsernameView.setError(getString(R.string.error_invalid_username));
            focusView = mUsernameView;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        } else if (!isPasswordLong(password)) {
            mPasswordView.setError(getString(R.string.error_too_short_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        /*
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }
        */


        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            if (InternetUtil.isConnected(LoginActivity.this)) {
                // Show a progress spinner, and kick off a background task to
                // perform the user login attempt.
                showProgress(true);
                //mAuthTask = new UserLoginTask(email, password);
                mAuthTask = new UserLoginTask(username, password, buttonEvent);
                mAuthTask.execute((Void) null);
            } else {
                new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Failed to connect with internet?")
                        .setContentText("Would you like to open the internet setting?")
                        .setCancelText("No,cancel plx!")
                        .setConfirmText("Yes,open it!")
                        .showCancelButton(true)
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                // reuse previous dialog instance, keep widget user state, reset them if you need
                                sDialog.dismissWithAnimation();
                            }
                        })
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                            }
                        })
                        .show();
            }
        }
    }

    private boolean isUsernameValid(String username) {
        String regex = "^[A-Za-z0-9]+$";
        boolean isValid = username.matches(regex);
        return isValid;
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        String regex = "^[A-Za-z0-9]+$";
        boolean isValid = false;
        if (password.matches(regex)) {
            isValid = true;
        }
        return isValid;
    }

    private boolean isPasswordLong(String password) {
        boolean isValid = false;
        if (password.length() > 4) {
            isValid = true;
        }
        return isValid;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /*
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }
    */

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        //mEmailView.setAdapter(adapter);
        mUsernameView.setAdapter(adapter);
    }


//    private interface ProfileQuery {
//        String[] PROJECTION = {
//                ContactsContract.CommonDataKinds.Email.ADDRESS,
//                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
//        };
//
//        int ADDRESS = 0;
//        int IS_PRIMARY = 1;
//    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Integer> {

        //private final String mEmail;
        private final String mUsername;
        private final String mPassword;
        private final int mButtonEvent;
        private String message;

        UserLoginTask(String username, String password, int buttonEvent) {
            //mEmail = email;
            mUsername = username;
            mPassword = password;
            mButtonEvent = buttonEvent;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            Integer RESULT = 0;
            if (mButtonEvent == SIGNINEVENT) {
                try {
                    // Simulate network access.
                    //Thread.sleep(2000);
                    //LogUtil.d(TAG, "UserLoginTask debug, Start to Sign In");
                    boolean isOK = LeanCloudHelper.loginWithUsername(mUsername, mPassword);
                    //AVUser.logIn(mUsername, mPassword);
                    //LogUtil.d(TAG, "UserLoginTask debug, Sign In Successful");
                    if (isOK)
                        RESULT = 1;
                    else
                        RESULT = -1;
                } catch (Exception e) {
                    message = e.getMessage();
                    LogUtil.e(TAG, "UserLoginTask error, Sign In Failed", e);
                    RESULT = -1;
                }
            } else if (mButtonEvent == SIGNUPEVENT) {
                try {
//                    LogUtil.d(TAG, "UserLoginTask debug, Start to Sign Up");
//                    AVUser user = new AVUser();// 新建 AVUser 对象实例
//                    user.setUsername(mUsername);// 设置用户名
//                    user.setPassword(mPassword);// 设置密码
//                    //user.setEmail("tom@leancloud.cn");// 设置邮箱
//                    user.signUp();
//                    LogUtil.d(TAG, "UserLoginTask debug, Sign Up Successful");
                    boolean isOK = LeanCloudHelper.signUpWithUsername(mUsername, mPassword);
                    if (isOK)
                        RESULT = 2;
                    else
                        RESULT = -2;
                } catch (Exception e) {
                    message = e.getMessage();
                    LogUtil.e(TAG, "UserLoginTask error, Sign Up Failed", e);
                    RESULT = -2;
                }
            }
            return RESULT;
        }

        @Override
        protected void onPostExecute(final Integer result) {
            mAuthTask = null;
            showProgress(false);

            if (result == 1 || result == 2) {
                startMainActivity();
            } else if (result == -1) {//Sign In
                mUsernameView.setError(message);
                mUsernameView.requestFocus();
            } else if (result == -2) {//Sign Up
                mUsernameView.setError(message);
                mUsernameView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }


}

