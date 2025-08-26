package com.dogus.otomat.icecdemo;

import com.tcn.icecboard.control.GroupInfo;
import com.tcn.icecboard.control.TcnConstantLift;
import com.tcn.icecboard.control.TcnVendIF;

import java.util.ArrayList;
import java.util.List;

public class UIComBack {
    private static final String TAG = "UIComBack";
    public static String[] HEAT_COOL_OFF_SWITCH_SELECT = {"制冷","加热","关闭"};
    private static UIComBack m_Instance = null;

    private List<GropInfoBack> m_GrpShowListAll = new ArrayList<GropInfoBack>();
    private List<GropInfoBack> m_GrpShowListHefanZp = new ArrayList<GropInfoBack>();

    public static synchronized UIComBack getInstance() {
        if (null == m_Instance) {
            m_Instance = new UIComBack();
        }
        return m_Instance;
    }

    public int getGroupHefanZpId(String data) {
        int iId = -1;
        if ((null == data) || (data.length() < 1)) {
            return iId;
        }

        for (GropInfoBack info : m_GrpShowListHefanZp) {
            if (data.equals(info.getShowText())) {
                iId = info.getGrpID();
            }
        }
        return iId;
    }

    public boolean isMutiGrpHefanZp() {
        boolean bRet = false;
        return bRet;
    }

    public List<GropInfoBack> getGroupListAll() {
        m_GrpShowListAll.clear();
        List<GroupInfo> mGroupInfoList = TcnVendIF.getInstance().getGroupListAll();
        if ((mGroupInfoList != null) && (mGroupInfoList.size() > 1)) {
            for (int i = 0; i < mGroupInfoList.size(); i++) {
                GroupInfo info = mGroupInfoList.get(i);
                GropInfoBack mGropInfoBack = new GropInfoBack();
                mGropInfoBack.setID(i);
                mGropInfoBack.setGrpID(info.getID());
                if (info.getID() == 0) {
                    mGropInfoBack.setShowText("主柜");
                } else {
                    mGropInfoBack.setShowText("副柜"+info.getID());
                }
                m_GrpShowListAll.add(mGropInfoBack);
            }
        }
        return m_GrpShowListAll;
    }

    public String[] getGroupListHefanZpShow() {
        List<String> m_RetList = new ArrayList<String>();
        if (m_GrpShowListHefanZp.size() < 1) {
            // Empty implementation
        }

        for (GropInfoBack info : m_GrpShowListHefanZp) {
            m_RetList.add(info.getShowText());
        }
        if (m_RetList.size() < 1) {
            return null;
        }
        String[] m_RetArray = new String[m_RetList.size()];
        for (int i = 0; i < m_RetList.size(); i++) {
            m_RetArray[i] = m_RetList.get(i);
        }
        return m_RetArray;
    }

    public int getGroupHefanZpIdByIndex(int index) {
        int iId = -1;
        if ((index >= 0) && (index < m_GrpShowListHefanZp.size())) {
            GropInfoBack info = m_GrpShowListHefanZp.get(index);
            iId = info.getGrpID();
        }
        return iId;
    }

    public String getGroupHefanZpShowByIndex(int index) {
        String strRet = "";
        if ((index >= 0) && (index < m_GrpShowListHefanZp.size())) {
            GropInfoBack info = m_GrpShowListHefanZp.get(index);
            strRet = info.getShowText();
        }
        return strRet;
    }

    public int getGroupHefanZpCount() {
        return m_GrpShowListHefanZp.size();
    }

    public void clearGroupListHefanZp() {
        m_GrpShowListHefanZp.clear();
    }

    public void addGroupListHefanZp(GropInfoBack info) {
        if (info != null) {
            m_GrpShowListHefanZp.add(info);
        }
    }

    public int getQueryParameters(String data) {
        int iAddress = -1;
        if ((null == data) || (data.length() < 1)) {
            return iAddress;
        }

        for (int i = 0; i < (TcnConstantLift.LIFT_QUERY_PARAM_HEFAN_ZP).length; i++) {
            if ((TcnConstantLift.LIFT_QUERY_PARAM_HEFAN_ZP[i]).equals(data)) {
                iAddress = i;
                break;
            }
        }

        return iAddress;
    }
}
