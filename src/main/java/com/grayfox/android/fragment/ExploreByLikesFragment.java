package com.grayfox.android.fragment;

import android.location.Location;

import com.grayfox.android.client.RecommendationsApi;
import com.grayfox.android.client.model.Recommendation;
import com.grayfox.android.client.task.RecommendationsByFriendsLikesAsyncTask;
import com.grayfox.android.client.task.RecommendationsByLikesAsyncTask;

import java.lang.ref.WeakReference;

public class ExploreByLikesFragment extends BaseExploreFragment {

    private SearchTask searchTask;

    @Override
    public void onPause() {
        super.onPause();
        if (searchTask != null) searchTask.cancel(true);
    }

    @Override
    public void onLocationAcquired(Location location) {
        super.onLocationAcquired(location);
        searchTask = new SearchTask(this);
        searchTask.transportation(RecommendationsApi.Transportation.DRIVING) // TODO: Hardcoded value
                .radius(50_000) // TODO: Hardcoded value
                .location(getCurrentLocation())
                .request();
    }

    private static class SearchTask extends RecommendationsByLikesAsyncTask {

        private WeakReference<ExploreByLikesFragment> reference;

        private SearchTask(ExploreByLikesFragment fragment) {
            super(fragment.getActivity().getApplicationContext());
            reference = new WeakReference<>(fragment);
        }

        @Override
        protected void onPreExecute() throws Exception {
            ExploreByLikesFragment fragment = reference.get();
            if (fragment != null) fragment.onPreSearchRecommendations();
        }

        @Override
        protected void onSuccess(Recommendation[] recommendations) throws Exception {
            ExploreByLikesFragment fragment = reference.get();
            if (fragment != null) fragment.onRecommendationsAcquired(recommendations);
        }

        @Override
        protected void onFinally() throws RuntimeException {
            ExploreByLikesFragment fragment = reference.get();
            if (fragment != null) fragment.onSearchRecommendationsFinally();
        }
    }
}