package cuban.social.testnetwork.groupchat.fragments;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

import cuban.social.testnetwork.ChatFragment;
import cuban.social.testnetwork.R;
import cuban.social.testnetwork.app.App;
import cuban.social.testnetwork.constants.Constants;
import cuban.social.testnetwork.dialogs.MsgImageChooseDialog;
import cuban.social.testnetwork.groupchat.adapters.MessageAdapter;
import cuban.social.testnetwork.groupchat.models.GroupChatMessage;
import cuban.social.testnetwork.util.AppConstant;
import cuban.social.testnetwork.util.BitmapUtils;
import github.ankushsachdeva.emojicon.EditTextImeBackListener;
import github.ankushsachdeva.emojicon.EmojiconEditText;
import github.ankushsachdeva.emojicon.EmojiconGridView;
import github.ankushsachdeva.emojicon.EmojiconsPopup;
import github.ankushsachdeva.emojicon.emoji.Emojicon;

public class GroupChatFragment extends Fragment implements Constants{
    private MessageAdapter adapter;
    private ListView listView;
    private String loggedInUserNickName = "";
    private String loggedInUserFullName = "";
    String gid;
    private static Boolean loading = false;

    private  String selectedChatImg = "";

    ImageView mAddImg, mEmojiBtn;
    ImageView mPreviewImg;
    LinearLayout mContainerImg;
    EmojiconEditText mMessageText; //= (EmojiconEditText) rootView.findViewById(R.id.messageText);
    private ProgressDialog pDialog;

    private  Uri selectedImage;
    private Uri outputFileUri;

    EmojiconsPopup popup;

    private  boolean isGallery=false;

    public GroupChatFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initpDialog();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        gid=getActivity().getIntent().getStringExtra("GroupID");

        final View rootView=inflater.inflate(R.layout.fragment_groupchat_room,container,false);

        popup = new EmojiconsPopup(rootView, getActivity());

        popup.setSizeForSoftKeyboard();

        popup.setOnEmojiconClickedListener(new EmojiconGridView.OnEmojiconClickedListener() {

            @Override
            public void onEmojiconClicked(Emojicon emojicon) {

                mMessageText.append(emojicon.getEmoji());
            }
        });

        popup.setOnEmojiconBackspaceClickedListener(new EmojiconsPopup.OnEmojiconBackspaceClickedListener() {

            @Override
            public void onEmojiconBackspaceClicked(View v) {

                KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
                mMessageText.dispatchKeyEvent(event);
            }
        });

        popup.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {

                setIconEmojiKeyboard();
            }
        });

        popup.setOnSoftKeyboardOpenCloseListener(new EmojiconsPopup.OnSoftKeyboardOpenCloseListener() {

            @Override
            public void onKeyboardOpen(int keyBoardHeight) {

            }

            @Override
            public void onKeyboardClose() {

                if (popup.isShowing())

                    popup.dismiss();
            }
        });

        popup.setOnEmojiconClickedListener(new EmojiconGridView.OnEmojiconClickedListener() {

            @Override
            public void onEmojiconClicked(Emojicon emojicon) {

                mMessageText.append(emojicon.getEmoji());
            }
        });

        popup.setOnEmojiconBackspaceClickedListener(new EmojiconsPopup.OnEmojiconBackspaceClickedListener() {

            @Override
            public void onEmojiconBackspaceClicked(View v) {

                KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
                mMessageText.dispatchKeyEvent(event);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        mMessageText = (EmojiconEditText) rootView.findViewById(R.id.input);
        listView = (ListView) rootView.findViewById(R.id.list);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMessageText.getText().toString().trim().equals("") && selectedChatImg.length() == 0) {
                    Toast.makeText(getActivity(), "Please enter some texts!", Toast.LENGTH_SHORT).show();
                } else {
                    if (selectedChatImg.length() != 0) {
                        loading = true;
                        showpDialog();
                        File files;
                        if(isGallery){
                            String path= AppConstant.getRealPathFromURI(getActivity(), selectedImage);
                            if (path.contains(".gif")){
                                files=new File(path);
                            }else{
                                files = new File(Environment.getExternalStorageDirectory() + File.separator + APP_TEMP_FOLDER, "msg.jpg");
                            }
                            Log.e("path",">>"+path);
                        }else{
                            files = new File(Environment.getExternalStorageDirectory() + File.separator + APP_TEMP_FOLDER, "msg.jpg");
                        }
                        uploadFile(files);
                    }else{
                        send();
                    }
                }
            }
        });
        mAddImg=(ImageView)rootView.findViewById(R.id.addImg);
        mAddImg.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                choiceImage();
            }
        });
        mEmojiBtn = (ImageView) rootView.findViewById(R.id.emojiBtn);

        mPreviewImg = (ImageView) rootView.findViewById(R.id.previewImg);
        if (selectedChatImg != null && selectedChatImg.length() > 0) {

            mPreviewImg.setImageURI(Uri.fromFile(new File(selectedChatImg)));
            mContainerImg.setVisibility(View.VISIBLE);
        }
        mContainerImg = (LinearLayout) rootView.findViewById(R.id.container_img);
        mContainerImg.setVisibility(View.GONE);


        if (!EMOJI_KEYBOARD) {

            mEmojiBtn.setVisibility(View.GONE);
        }
        mEmojiBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (!popup.isShowing()) {

                    if (popup.isKeyBoardOpen()){

                        popup.showAtBottom();
                        setIconSoftKeyboard();

                    } else {

                        mMessageText.setFocusableInTouchMode(true);
                        mMessageText.requestFocus();
                        popup.showAtBottomPending();

                        final InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.showSoftInput(mMessageText, InputMethodManager.SHOW_IMPLICIT);
                        setIconSoftKeyboard();
                    }

                } else {

                    popup.dismiss();
                }
            }
        });

        EditTextImeBackListener er = new EditTextImeBackListener() {

            @Override
            public void onImeBack(EmojiconEditText ctrl, String text) {

                hideEmojiKeyboard();
            }
        };

        mMessageText.setOnEditTextImeBackListener(er);

        showAllOldMessages();
        return rootView;
    }

    private String messageImg="";

    private void uploadFile(File files) {
        Uri file = Uri.fromFile(files);
        StorageReference riversRef = FirebaseStorage.getInstance().getReference().child("images/"+file.getLastPathSegment());
        UploadTask uploadTask = riversRef.putFile(file);

// Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                hidepDialog();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                hidepDialog();
                messageImg=downloadUrl.toString();
                send();
            }
        });
    }

    private void initpDialog(){
        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage(getString(R.string.msg_loading));
        pDialog.setCancelable(false);
    }

    private void showpDialog() {
        if (!pDialog.isShowing()) pDialog.show();
    }

    protected void hidepDialog() {

        if (pDialog.isShowing()) pDialog.dismiss();
    }

    private void send() {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("group_chat").child(gid);
        ref.addValueEventListener(new ValueEventListener(){

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        DatabaseReference ref1=ref.push();
        ref1.setValue(new GroupChatMessage(mMessageText.getText().toString(),
                        messageImg,     //attachedImageurl
                        App.getInstance().getUsername(),//nickname
                        App.getInstance().getPhotoUrl(),//photo_url
                        App.getInstance().getId())
                );
        ref1.child("messageTime").setValue(ServerValue.TIMESTAMP);
        mMessageText.setText("");
        messageImg="";
        mContainerImg.setVisibility(View.GONE);
    }

    private void showAllOldMessages() {
        loggedInUserNickName = App.getInstance().getUsername();
        loggedInUserFullName = App.getInstance().getFullname();

        adapter = new MessageAdapter(this.getActivity(), R.layout.item_in_message, FirebaseDatabase.getInstance().getReference("group_chat").child(gid));
        listView.setAdapter(adapter);
    }

    public void choiceImage(){
        android.app.FragmentManager fm = getActivity().getFragmentManager();

        MsgImageChooseDialog alert = new MsgImageChooseDialog();

        alert.show(fm, "alert_dialog_image_choose");
    }

    public void imageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, getText(R.string.label_select_img)), SELECT_CHAT_IMG);
    }

    public void imageFromCamera() {
        try {

            File root = new File(Environment.getExternalStorageDirectory(), APP_TEMP_FOLDER);

            if (!root.exists()) {

                root.mkdirs();
            }

            File sdImageMainDirectory = new File(root, "msg.jpg");
            outputFileUri = Uri.fromFile(sdImageMainDirectory);

            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            startActivityForResult(cameraIntent, CREATE_CHAT_IMG);

        } catch (Exception e) {

            Toast.makeText(getActivity(), "Error occured. Please try again later.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SELECT_CHAT_IMG:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    isGallery=true;
                    selectedImage = data.getData();
                    selectedChatImg = ChatFragment.getImageUrlWithAuthority(getActivity(), selectedImage, "msg.jpg");

                    try {


                        selectedChatImg = Environment.getExternalStorageDirectory() + File.separator + APP_TEMP_FOLDER + File.separator + "msg.jpg";
                        File file = new File(selectedChatImg);
                        BitmapUtils.rotateAndSaveImg(getActivity(),file,selectedImage);
                        mPreviewImg.setImageURI(null);
                        mPreviewImg.setImageURI(Uri.fromFile(file));
                        mContainerImg.setVisibility(View.VISIBLE);
                    } catch (Exception e) {

                        Log.e("OnSelectPostImage", e.getMessage());
                    }
                }
                break;
            case CREATE_CHAT_IMG:
                if (resultCode == Activity.RESULT_OK) {
                    isGallery=false;
                    try {

                        selectedChatImg = Environment.getExternalStorageDirectory() + File.separator + APP_TEMP_FOLDER + File.separator + "msg.jpg";
                        File file = new File(selectedChatImg);
                        BitmapUtils.rotateAndSaveImg(getActivity(),file,selectedImage);
                        mPreviewImg.setImageURI(Uri.fromFile(new File(selectedChatImg)));
                        mContainerImg.setVisibility(View.VISIBLE);
                    } catch (Exception ex) {

                        Log.v("OnCameraCallBack", ex.getMessage());
                    }
                }
                break;
        }
    }

    private  void scrollListViewToBottom() {

        listView.smoothScrollToPosition(adapter.getCount());

        listView.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                listView.setSelection(adapter.getCount() - 1);
            }
        });
    }

    public void setIconEmojiKeyboard() {

        mEmojiBtn.setBackgroundResource(R.drawable.ic_emoji);
    }

    public void setIconSoftKeyboard() {

        mEmojiBtn.setBackgroundResource(R.drawable.ic_keyboard);
    }

    public void hideEmojiKeyboard() {

        popup.dismiss();
    }

    public void onDestroyView() {

        super.onDestroyView();
        hidepDialog();
    }
}
