package org.sensation.snapmemo.httpservice;

import org.sensation.snapmemo.VO.MemoVO;
import org.sensation.snapmemo.tool.Resource_stub;

import java.io.OutputStream;

/**
 * Created by Alan on 2016/2/5.
 */
public class HttpService_stub {
    public MemoVO transPic(OutputStream os) {
        return new Resource_stub().getMemoVOs().get(0);
    }
}
