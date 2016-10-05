package in.reweyou.reweyou;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import in.reweyou.reweyou.classes.CameraModule;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CameraFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CameraFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CameraFragment extends Fragment {

    // You create an instance of the module. I use a singleton.
    CameraModule mCameraModule = new CameraModule();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera, container, false);

        TextureView mTextureView = (TextureView) view.findViewById(R.id.camera_preview);
        mTextureView.setSurfaceTextureListener(mCameraModule);

        return view;
    }
}