package org.sensation.snapmemo.server.Data;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.sensation.snapmemo.server.PO.UserPO;
import org.sensation.snapmemo.server.Utility.ResultMessage;

public class UserData{
	Session session;
	public UserData(){
		this.session = MySessionFactory.sessionFactory.openSession();
	}
	public ResultMessage addUser(UserPO user){
		if(findUserByID(user.getID())!=null)
			return new ResultMessage(false,"this user already exists!");
		else{
			session.beginTransaction();
			session.save(user);
			session.getTransaction().commit();
			return new ResultMessage(true,"success");
		}

	}
	public ResultMessage deleteUser(String userID){
		if(findUserByID(userID)==null)
			return new ResultMessage(false,"this user doesn't exist!");
		else{
			session.beginTransaction();
			UserPO tmp = new UserPO();
			tmp.setID(userID);
			session.delete(tmp);
			session.getTransaction().commit();
			return new ResultMessage(true,"success");
		}
	}
	public ResultMessage updateUser(UserPO user){
		if(findUserByID(user.getID())==null)
			return new ResultMessage(false,"this user doesn't exist!");
		else{
			session.beginTransaction();
			session.update(user);
			session.getTransaction().commit();
			return new ResultMessage(true,"success");
		}
	}
	public UserPO findUserByID(String userID){
		session.beginTransaction();
		Criteria cri = session.createCriteria(UserPO.class);
		cri.add(Restrictions.eq("userID", userID));
		if(cri.list().isEmpty())
			return null;
		else
			return (UserPO) cri.list().get(0);
	}
	public UserPO findUserByName(String userName){
		session.beginTransaction();
		Criteria cri = session.createCriteria(UserPO.class);
		cri.add(Restrictions.eq("userName", userName));
		if(cri.list().isEmpty())
			return null;
		else
			return (UserPO) cri.list().get(0);
	}
	public static void main(String[] args){
		UserPO user1 = new UserPO(null,"Alan","123456");
		MySessionFactory factory = new MySessionFactory();
		UserData data = new UserData();
		Session session = data.session;
		session.beginTransaction();
		session.save(user1);
		session.getTransaction().commit();
	}

}
