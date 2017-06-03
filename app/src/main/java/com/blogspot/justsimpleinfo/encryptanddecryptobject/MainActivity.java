package com.blogspot.justsimpleinfo.encryptanddecryptobject;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;
import javax.crypto.spec.SecretKeySpec;

public class MainActivity extends AppCompatActivity  implements View.OnClickListener{

        final static String DIR = "EcryptedObject";

        final static String PATH =  Environment.getExternalStorageDirectory()+"/"+DIR+"/credential.lau";

        final static String ENCRYPTION_PASSWORD = "PasswordPassword";


        EditText mUserNameEditText;
        EditText mPasswordEditText;
        Button mEncryptAndSaveObjectBtn;
        Button mDecryptAndShowObjectBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUserNameEditText = (EditText) this.findViewById(R.id.username_edittext);
        mPasswordEditText = (EditText) this.findViewById(R.id.password_edittext);

        mEncryptAndSaveObjectBtn = (Button) this.findViewById(R.id.save_btn);
        mEncryptAndSaveObjectBtn.setOnClickListener(this);

        mDecryptAndShowObjectBtn = (Button) this.findViewById(R.id.show_btn);
        mDecryptAndShowObjectBtn.setOnClickListener(this);

    }
        @Override
        public void onClick(View view) {

            int id = view.getId();
            switch (id){
                case R.id.save_btn:

                    String userName = mUserNameEditText.getText().toString();
                    String password = mPasswordEditText.getText().toString();

                    CredentialObject credentialObject = new CredentialObject();
                    credentialObject.setUserName(userName);
                    credentialObject.setPassword(password);

                    mUserNameEditText.setText("");
                    mPasswordEditText.setText("");




                    try {

                        saveCredentialObject(credentialObject);
                        Toast.makeText(this,"Credential Encrypted Successfully.",Toast.LENGTH_SHORT).show();

                    } catch (NoSuchPaddingException e) {

                        Toast.makeText(this,"Failed to save",Toast.LENGTH_SHORT).show();

                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {

                        Toast.makeText(this,"Failed to save",Toast.LENGTH_SHORT).show();

                        e.printStackTrace();
                    } catch (InvalidKeyException e) {

                        Toast.makeText(this,"Failed to save",Toast.LENGTH_SHORT).show();

                        e.printStackTrace();
                    } catch (IOException e) {

                        Toast.makeText(this,"Failed to save",Toast.LENGTH_SHORT).show();

                        e.printStackTrace();
                    } catch (IllegalBlockSizeException e) {

                        Toast.makeText(this,"Failed to save",Toast.LENGTH_SHORT).show();

                        e.printStackTrace();
                    }

                    break;
                case R.id.show_btn:

                    try {
                        decrypt();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (NoSuchPaddingException e) {
                        e.printStackTrace();
                    } catch (InvalidKeyException e) {
                        e.printStackTrace();
                    }

                    break;
                default:
            }
        }

    /**
     *
     * @param credentialObject
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws IOException
     * @throws IllegalBlockSizeException
     */
        private void saveCredentialObject(CredentialObject credentialObject) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IOException, IllegalBlockSizeException {


            File dir = new File(Environment.getExternalStorageDirectory()+"/"+DIR);

            if (!dir.exists()) {
                /**
                 * create folder if not exists
                 */
                dir.mkdir();

            }



            SecretKeySpec sks = new SecretKeySpec(ENCRYPTION_PASSWORD.getBytes(),"AES/ECB/PKCS5Padding");

            /**
             *  Create cipher
             */

            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, sks);
            SealedObject sealedObject = new SealedObject(credentialObject, cipher);
            /**
             * Wrap the output stream
             */

            CipherOutputStream cos = new CipherOutputStream(new BufferedOutputStream(new FileOutputStream(PATH)), cipher);

            ObjectOutputStream outputStream = new ObjectOutputStream(cos);
            outputStream.writeObject(sealedObject);
            outputStream.close();

        }

    /**
     *
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     */
    private void decrypt()
            throws IOException, NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException {

        SecretKeySpec sks = new SecretKeySpec(ENCRYPTION_PASSWORD.getBytes(),"AES");

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, sks);

        CipherInputStream cipherInputStream = new CipherInputStream(new BufferedInputStream(new FileInputStream(PATH)), cipher);
        ObjectInputStream inputStream = new ObjectInputStream(cipherInputStream);
        SealedObject sealedObject = null;
        try {
            sealedObject = (SealedObject) inputStream.readObject();

            CredentialObject credentialObject = (CredentialObject) sealedObject.getObject(cipher);

            TextView usernameDisplayTextView = (TextView) this.findViewById(R.id.username_display_textview);
            TextView passwordDisplayTextView = (TextView) this.findViewById(R.id.password_display_textview);
            usernameDisplayTextView.setText("Username : "+credentialObject.getUserName());
            passwordDisplayTextView.setText("Password : "+credentialObject.getPassword());

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }finally{
            inputStream.close();
        }

    }

    }
