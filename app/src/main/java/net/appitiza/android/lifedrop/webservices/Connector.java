package net.appitiza.android.lifedrop.webservices;


import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


class Connector {
   private static Connector sConnector;

   private Connector() {

   }

   protected static Connector getInstance() {

       if (sConnector == null) {
           sConnector = new Connector();
       }

       return sConnector;
   }

   protected Retrofit getConnector() {
       OkHttpClient httpClient = new OkHttpClient.Builder()
               .connectTimeout(60, TimeUnit.SECONDS)
               .readTimeout(60, TimeUnit.SECONDS)
               .build();
       return new Retrofit.Builder()
               .baseUrl(UrlConstants.BASE_URL)
               .addConverterFactory(GsonConverterFactory.create())
               .client(httpClient)
               .build();

   }



   public void clearConnection() {
       sConnector = null;
   }
}
