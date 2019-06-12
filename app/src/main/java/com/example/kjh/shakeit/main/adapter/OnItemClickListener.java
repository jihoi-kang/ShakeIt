package com.example.kjh.shakeit.main.adapter;

import com.example.kjh.shakeit.data.User;

/**------------------------------------------------------------------
 인터페이스 ==> 친구 목록 클릭시 발생하는 이벤트
 ------------------------------------------------------------------*/
public interface OnItemClickListener {

    void onItemClick(User user, boolean isChecked);

}
