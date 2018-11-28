package com.depart.cea.test1;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MyJobService extends JobService {
    private String TAG = "MyJobService";
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Toast.makeText(this, "MyJobService.onStartJob: ", Toast.LENGTH_SHORT).show();
        Log.i(TAG, "onStartJob: ");
//        CollectionService.mLocationClient.restart();
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}
