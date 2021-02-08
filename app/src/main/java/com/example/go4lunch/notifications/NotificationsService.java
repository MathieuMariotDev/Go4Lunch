package com.example.go4lunch.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.Interface.CallBackFetchRequest;
import com.example.go4lunch.LoginActivity;
import com.example.go4lunch.MainActivity;
import com.example.go4lunch.R;
import com.example.go4lunch.Utils.FetchPlaceRequestUtil;
import com.example.go4lunch.api.WorkmateHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class NotificationsService extends FirebaseMessagingService {

    private final int NOTIFICATION_ID = 007;
    private final String NOTIFICATION_TAG = "FIREBASEOC";
    private String idSelected;
    private PlacesClient mPlacesClient;
    private String apiKey = BuildConfig.API_KEY;
    private String workmates;
    private String nameRestaurant;
    private String adressRestaurant;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getNotification() != null) {
            getUserSelected();
        }
    }

    // ---

    private void sendVisualNotification() {

        // 1 - Create an Intent that will be shown when user will click on the Notification
        Intent intent = new Intent(this, LoginActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        // 2 - Create a Style for the Notification
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(getString(R.string.app_name));
        inboxStyle.addLine(nameRestaurant);
        inboxStyle.addLine(adressRestaurant);
        inboxStyle.addLine(getString(R.string.workmate_lbl_notif)
                + workmates);


        // 3 - Create a Channel (Android 8)
        String channelId = getString(R.string.default_notification_channel_id);

        // 4 - Build a Notification object
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_logo_go4lunch)
                        //.setContentTitle(getString(R.string.app_name))
                        //.setContentText(getString(R.string.notif_txt))
                        .setAutoCancel(true)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setContentIntent(pendingIntent)
                        .setStyle(inboxStyle);

        // 5 - Add the Notification to the Notification Manager and show it.
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // 6 - Support Version >= Android 8
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "Message provenant de Firebase";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        // 7 - Show notification
        notificationManager.notify(NOTIFICATION_TAG, NOTIFICATION_ID, notificationBuilder.build());
    }

    @Nullable
    protected FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public void getUserSelected() {
        FetchPlaceRequestUtil mFetchPlaceRequestUtil = new FetchPlaceRequestUtil();
        if (getCurrentUser() != null) {
            WorkmateHelper.getUser(getCurrentUser().getUid()).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (!task.getResult().getString("idSelectedRestaurant").equals("No place selected")) {
                        idSelected = task.getResult().getString("idSelectedRestaurant");
                        initPlacesClient();
                        mFetchPlaceRequestUtil.placeRequestForNotification(idSelected, mPlacesClient, new CallBackFetchRequest() {
                            @Override
                            public void onFetchPlaceCallBack(Place place) {
                                nameRestaurant = place.getName();
                                adressRestaurant = place.getAddress();
                                getWorkmate();
                            }

                            @Override
                            public void onFetchPhotoCallBack(Bitmap mPicture) {
                                return;
                            }
                        });
                    } else if (task.getResult().getString("idSelectedRestaurant").equals("No place selected")) {

                    }
                }
            });
        }
    }

    public void getWorkmate() {
        WorkmateHelper.getUsersCollection().whereEqualTo("idSelectedRestaurant", idSelected).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override//Don't need to use snaphot listener
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot workmate : task.getResult().getDocuments()) {
                    workmates = workmate.getString("username") + " / ";
                }
                sendVisualNotification();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void initPlacesClient() {
        Places.initialize(getApplicationContext(), apiKey);

        // Create a new PlacesClient instance
        mPlacesClient = Places.createClient(getApplicationContext());

    }


}
