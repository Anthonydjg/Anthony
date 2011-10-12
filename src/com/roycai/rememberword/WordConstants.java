package com.roycai.rememberword;

import android.provider.BaseColumns;

public class WordConstants implements BaseColumns {
    
    public static final int WORD_STATUS_ALREADY_REMEMBER = 0x01;	//�Ѽ�ס
    public static final int WORD_STATUS_REMEMBERING = 0x02;			//������
    public static final int WORD_STATUS_NOT_REMEMBER = 0x03;		//��ȫ���ǵ�
    
    public static final int WORD_IMPORTANCE_IMPORTANT = 0x01;		//��Ҫ��
    public static final int WORD_IMPORTANCE_MEDIUM = 0x02;			//�еȵ�
    public static final int WORD_IMPORTANCE_UNIMPORTANCE= 0x03;		//����Ҫ��
}
