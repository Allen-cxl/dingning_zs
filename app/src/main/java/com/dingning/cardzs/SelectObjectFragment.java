package com.dingning.cardzs;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.dingning.cardzs.adapter.ObjectAdapter;
import com.dingning.cardzs.api.Parameter;
import com.dingning.cardzs.model.Parent;
import com.dingning.cardzs.model.Student;
import com.dingning.cardzs.weight.SpacesItemDecoration;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.ButterKnife;


public class SelectObjectFragment extends BaseFragment implements ObjectAdapter.OnItemClickListener{

    private static final String send_type = "type";
    private int type;

    private View mView;
    private RecyclerView mRecyclerView;
    private ObjectAdapter mAdapter;
    private Button mBtRecord;
    private Button mBtBack;
    private List<Parent> parents;
    private Button mBtnAllOrNo;
    private boolean isAllSelected=false;
    private int spacingInPixels = 20;
    private Set<String> mParentIds;
    private Student student;

    public SelectObjectFragment() {

    }

    public static SelectObjectFragment newInstance(int type) {
        SelectObjectFragment fragment = new SelectObjectFragment();
        Bundle args = new Bundle();
        args.putInt(send_type, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = getArguments().getInt(send_type);
        student = application.getStudent();
        mParentIds = new HashSet<>();
        parents = student.getParent();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(mView == null){
            mView = inflater.inflate(R.layout.fragment_select_object, container, false);
            initView();
            initData();
        }

        ViewGroup parent = (ViewGroup) mView.getParent();
        if (parent != null) {
            parent.removeView(mView);
        }
        return mView;
    }

    private void initData() {


        mBtnAllOrNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int leng = mAdapter.getItemCount();
                if (isAllSelected){
                    for (int i=0;i<leng;i++){
                        Parent object =mAdapter.getItem(i);
                        object.setSelected(false);
                        mParentIds.remove(object.getParent_id());
                        mAdapter.notifyDataSetChanged();
                    }
                    isAllSelected=false;
                }else {
                    for (int i=0;i<leng;i++){
                        Parent object =mAdapter.getItem(i);
                        object.setSelected(true);
                        mParentIds.remove(object.getParent_id());
                        mParentIds.add(object.getParent_id());
                        mAdapter.notifyDataSetChanged();
                    }
                    isAllSelected=true;
                }
            }
        });
        if(type == Parameter.VIDEO){
            mBtRecord.setText(getString(R.string.record_video));
            mBtRecord.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.corner_pink));
        }else if(type == Parameter.VOICE){
            mBtRecord.setText(getString(R.string.record_voice));
            mBtRecord.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.corner_green));
        }
        mBtRecord.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {

                if(mParentIds.isEmpty()){
                    Toast.makeText(getActivity(), getString(R.string.select_send_object), Toast.LENGTH_LONG).show();
                    return;
                }
                if(type == Parameter.VIDEO){
                    enterFragment(RecordingVideoFragment.newInstance(mParentIds));
                }else if(type == Parameter.VOICE){
                    enterFragment(RecordingVoiceFragment.newInstance(mParentIds));
                }
            }
        });
        mBtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBack();
            }
        });

    }

    private void initView() {
        //得到控件
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.recyclerview);
        //设置布局管理器
        GridLayoutManager linearLayoutManager = new GridLayoutManager(getActivity(), 7, GridLayoutManager.VERTICAL, false);
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels, SpacesItemDecoration.GridLayout));
        mRecyclerView.setLayoutManager(linearLayoutManager);
        //设置适配器
        mAdapter = new ObjectAdapter(getContext(), parents);
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
        mBtnAllOrNo= (Button) mView.findViewById(R.id.bt_select_cancel);
        mBtRecord= (Button) mView.findViewById(R.id.bt_record_voice);
        mBtBack= (Button) mView.findViewById(R.id.bt_back);
    }

    @Override
    public void onItemClick(ObjectAdapter.ViewHolder vh, int position) {
        Parent item=mAdapter.getItem(position);
        if (item.isSelected()){
            mParentIds.remove(item.getParent_id());
            item.setSelected(false);
            vh.ivselect.setImageDrawable(getResources().getDrawable(R.drawable.off_select));
        }else {
            mParentIds.add(item.getParent_id());
            item.setSelected(true);
            vh.ivselect.setImageDrawable(getResources().getDrawable(R.drawable.on_select));
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemLongClick(ObjectAdapter.ViewHolder vh, int position) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        clearSelect();
        mParentIds.clear();
        mParentIds = null;
    }

    public void clearSelect(){

        if(parents != null){
            for (Parent p: parents) {
                p.setSelected(false);
            }
        }
    }
}
