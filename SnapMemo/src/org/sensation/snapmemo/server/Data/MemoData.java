package org.sensation.snapmemo.server.Data;

import java.util.Iterator;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.sensation.snapmemo.server.PO.MemoPO;
import org.sensation.snapmemo.server.Utility.ResultMessage;

public class MemoData{
	Session session;
	public MemoData(){
		this.session = MySessionFactory.sessionFactory.openSession();
	}
	public ResultMessage addMemo(MemoPO memo){
		if(findMemo(memo.getMemoID())!=null)
			return new ResultMessage(false,"this memo already exists!");
		else{
			session.beginTransaction();
			session.save(memo);
			session.getTransaction().commit();
			return new ResultMessage(true,"success");
		}

	}
	public ResultMessage deleteMemo(String memoID){
		if(findMemo(memoID)==null)
			return new ResultMessage(false,"this memo doesn't exist!");
		else{
			session.beginTransaction();
			MemoPO tmp = new MemoPO();
			tmp.setMemoID(memoID);
			session.clear();
			session.delete(tmp);
			session.getTransaction().commit();
			return new ResultMessage(true,"success");
		}
	}
	public ResultMessage updateMemo(MemoPO memo){
		if(findMemo(memo.getMemoID())==null)
			return new ResultMessage(false,"this memo doesn't exist!");
		else{
			session.beginTransaction();
			session.clear();
			session.update(memo);
			session.getTransaction().commit();
			return new ResultMessage(true,"success");
		}
	}
	public MemoPO findMemo(String memoID){
		session.beginTransaction();
		Criteria cri = session.createCriteria(MemoPO.class);
		cri.add(Restrictions.eq("memoID", memoID));
		if(cri.list().isEmpty())
			return null;
		else
			return (MemoPO) cri.list().get(0);
	}
	
	@SuppressWarnings("unchecked")
	public Iterator<MemoPO> getMemoList(String userID){
		session.beginTransaction();
		Criteria cri = session.createCriteria(MemoPO.class);
		cri.add(Restrictions.eq("userID", userID));
		return (Iterator<MemoPO>)cri.list().iterator();
	}
}
