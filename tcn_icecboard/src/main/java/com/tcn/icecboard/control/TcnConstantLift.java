package com.tcn.icecboard.control;

import android.content.Context;

import com.tcn.icecboard.R;

public class TcnConstantLift {
	private static Context mContext = null;
	public static String[] LIFT_QUERY_PARAM_HEFAN_ZP = null;


	public static void init(Context context) {
		mContext = context;
		LIFT_QUERY_PARAM_HEFAN_ZP = new String[] {
				mContext.getString(R.string.background_hefan_zp_param_0),mContext.getString(R.string.background_hefan_zp_param_1),mContext.getString(R.string.background_hefan_zp_param_2),mContext.getString(R.string.background_hefan_zp_param_3),
				mContext.getString(R.string.background_hefan_zp_param_4),
				mContext.getString(R.string.background_hefan_zp_param_5),mContext.getString(R.string.background_hefan_zp_param_6),mContext.getString(R.string.background_hefan_zp_param_7),mContext.getString(R.string.background_hefan_zp_param_8),
				mContext.getString(R.string.background_hefan_zp_param_9),mContext.getString(R.string.background_hefan_zp_param_10),mContext.getString(R.string.background_hefan_zp_param_11), mContext.getString(R.string.background_hefan_zp_param_12),
				mContext.getString(R.string.background_hefan_zp_param_13),mContext.getString(R.string.background_hefan_zp_param_14),mContext.getString(R.string.background_hefan_zp_param_15),mContext.getString(R.string.background_hefan_zp_param_16),
				mContext.getString(R.string.background_hefan_zp_param_17),mContext.getString(R.string.background_hefan_zp_param_18), mContext.getString(R.string.background_hefan_zp_param_19),mContext.getString(R.string.background_hefan_zp_param_20),
				mContext.getString(R.string.background_hefan_zp_param_21),mContext.getString(R.string.background_hefan_zp_param_22),mContext.getString(R.string.background_hefan_zp_param_23),mContext.getString(R.string.background_hefan_zp_param_24),
				mContext.getString(R.string.background_hefan_zp_param_25), mContext.getString(R.string.background_hefan_zp_param_26),mContext.getString(R.string.background_hefan_zp_param_27),mContext.getString(R.string.background_hefan_zp_param_28),
				mContext.getString(R.string.background_hefan_zp_param_29), mContext.getString(R.string.background_hefan_zp_param_30),mContext.getString(R.string.background_hefan_zp_param_31),mContext.getString(R.string.background_hefan_zp_param_32),
				mContext.getString(R.string.background_hefan_zp_param_33),mContext.getString(R.string.background_hefan_zp_param_34), mContext.getString(R.string.background_hefan_zp_param_35),mContext.getString(R.string.background_hefan_zp_param_36),
				mContext.getString(R.string.background_hefan_zp_param_37),mContext.getString(R.string.background_hefan_zp_param_38),mContext.getString(R.string.background_hefan_zp_param_39), mContext.getString(R.string.background_hefan_zp_param_40),
				mContext.getString(R.string.background_hefan_zp_param_41),mContext.getString(R.string.background_hefan_zp_param_42),mContext.getString(R.string.background_hefan_zp_param_43),mContext.getString(R.string.background_hefan_zp_param_44),
				mContext.getString(R.string.background_hefan_zp_param_45),mContext.getString(R.string.background_hefan_zp_param_46),mContext.getString(R.string.background_hefan_zp_param_47),mContext.getString(R.string.background_hefan_zp_param_48),
				mContext.getString(R.string.background_hefan_zp_param_49), mContext.getString(R.string.background_hefan_zp_param_50),mContext.getString(R.string.background_hefan_zp_param_51),mContext.getString(R.string.background_hefan_zp_param_52),
				mContext.getString(R.string.background_hefan_zp_param_53),mContext.getString(R.string.background_hefan_zp_param_54), mContext.getString(R.string.background_hefan_zp_param_55),mContext.getString(R.string.background_hefan_zp_param_56),
				mContext.getString(R.string.background_hefan_zp_param_57),mContext.getString(R.string.background_hefan_zp_param_58),mContext.getString(R.string.background_hefan_zp_param_59), mContext.getString(R.string.background_hefan_zp_param_60),
				mContext.getString(R.string.background_hefan_zp_param_61),mContext.getString(R.string.background_hefan_zp_param_62),mContext.getString(R.string.background_hefan_zp_param_63),mContext.getString(R.string.background_hefan_zp_param_64),
				mContext.getString(R.string.background_hefan_zp_param_65),mContext.getString(R.string.background_hefan_zp_param_66),mContext.getString(R.string.background_hefan_zp_param_67),mContext.getString(R.string.background_hefan_zp_param_68),
				mContext.getString(R.string.background_hefan_zp_param_69),mContext.getString(R.string.background_hefan_zp_param_70),mContext.getString(R.string.background_hefan_zp_param_71),mContext.getString(R.string.background_hefan_zp_param_72),
				mContext.getString(R.string.background_hefan_zp_param_73),mContext.getString(R.string.background_hefan_zp_param_74),mContext.getString(R.string.background_hefan_zp_param_75),mContext.getString(R.string.background_hefan_zp_param_76),
				mContext.getString(R.string.background_hefan_zp_param_77),mContext.getString(R.string.background_hefan_zp_param_78),mContext.getString(R.string.background_hefan_zp_param_79),mContext.getString(R.string.background_hefan_zp_param_80),
				mContext.getString(R.string.background_hefan_zp_param_81),mContext.getString(R.string.background_hefan_zp_param_82) ,mContext.getString(R.string.background_hefan_zp_param_83),mContext.getString(R.string.background_hefan_zp_param_84),
				mContext.getString(R.string.background_hefan_zp_param_85),mContext.getString(R.string.background_hefan_zp_param_86),mContext.getString(R.string.background_hefan_zp_param_87),mContext.getString(R.string.background_hefan_zp_param_88),
				mContext.getString(R.string.background_hefan_zp_param_89),mContext.getString(R.string.background_hefan_zp_param_90)
		};
	}


	public static final String[] LIFT_FLOOR_DATA = {"0~回原点","1~升降至第一层","2~升降至第二层","3~升降至第三层","4~升降至第四层","5~升降至第五层","6~升降至第六层","7~升降至第七层","8~升降至第八层"
			,"9~升降至第九层","10~升降至第十层","11~升降至加热位置高度","12~升降至出货口高度","13~升降至平移不会干涉最大高度","14~升降顶住保持电流","20~平移回原点","21~平移至1列货道"
			,"22~平移至2列货道","23~平移至3列货道","24~平移至4列货道","25~平移至5列货道","26~平移至6列货道","27~平移至7列货道","28~平移至8列货道","29~平移至9列货道","30~平移至10列货道"
			,"31~平移至11列货道","32~平移至12列货道","33~平移至出货口位置"};

	public static final String[] LIFT_OUT_STEPS_STATUS = {"0","1"};

	public static final String[] LIFT_INPUT_DETECT = {"4","5","6","7","8","9","10"};
}
