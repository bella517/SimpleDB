package simpledb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LockManager {
	
	//check to see if the page has a shared lock on it, can be multiple transactions
	private HashMap <PageId, Set<TransactionId>> sharedLocksOnPage;
	
	//check to see if the page has an exclusive lock on it, can only be one transaction
	private HashMap <PageId, TransactionId> exclusiveLockOnPage;

	public LockManager(){
		sharedLocksOnPage = new HashMap <PageId, Set<TransactionId>>();
		exclusiveLockOnPage = new HashMap <PageId, TransactionId>();
	}
	
	public synchronized boolean setLock(TransactionId tid, PageId pid, Permissions perm){
		if (perm == Permissions.READ_ONLY)
			return setSharedLock(tid, pid);
		else
			return setExclusiveLock(tid, pid);
	}
	
	 private synchronized boolean setSharedLock(TransactionId tid, PageId pid){
		 //check to see if there is an exclusive lock on the page
		 TransactionId exLock = exclusiveLockOnPage.get(pid);
		 
		 //check to see if there is a shared lock on the page
		 Set<TransactionId> sharedLock = sharedLocksOnPage.get(pid);
		 
		 
		 if (exLock == null || exLock.equals(tid)) //no exclusive lock on the page already or same transaction requesting
		 {
			 if (sharedLock == null) //page has not been locked by any transaction
			 {
				 //need to create a list of transactionIDs since it is the first one
				 sharedLock = new HashSet<TransactionId>();
			 }
			 
			 sharedLock.add(tid); //adds that transaction to the list
			 sharedLocksOnPage.put(pid, sharedLock); //adds shared lock to the page
			 
			 return true;
		 }
		 return false;
	 }
	 
	 private synchronized boolean setExclusiveLock(TransactionId tid, PageId pid){
		 
		 TransactionId exLock = exclusiveLockOnPage.get(pid);
		 Set<TransactionId> sharedLock = sharedLocksOnPage.get(pid);
		 
		 //check to see if there are multiple shared locks already on the page
		 if (sharedLock != null && sharedLock.size() > 1){
			 System.out.println("multiple shared locks on page, cannot grant");
			 return false;
		 }
		 //if there is only one shared lock, check to see if it is a different transaction that holds this lock
		 //if it is you cannot upgrade 
		 if (sharedLock!= null && sharedLock.size() == 1 && !sharedLock.contains(tid)) {
			 System.out.println("different transaction has shared lock");
			 return false;
		 }
		 //check to see if another transaction holds an exclusive lock on the page
		 if (exLock != null && !exLock.equals(tid)) {
			 System.out.println("different transaction has exclusive lock");
			 return false;
		 }
		 //if we get to this statement that means all the conditions hold and we can grant lock request
		 else
		 {
			 System.out.println("lock granted");
			 exclusiveLockOnPage.put(pid, tid);
			 
			 return true;
		 }
			 
	 }
	 
	 public synchronized void releaseLock(TransactionId tid, PageId pid) {
		 Set sharedLockedTIDs = sharedLocksOnPage.get(pid);
		 TransactionId exLockedPages = exclusiveLockOnPage.get(pid);
				 
		 //check to see if there are any shared locks on this page 
		 if (sharedLockedTIDs != null)
		 {
			 sharedLockedTIDs.remove(pid);
			 sharedLocksOnPage.put(pid, sharedLockedTIDs);
		 }
		 
		 exclusiveLockOnPage.remove(pid);
		 
	 }
	 
	 public synchronized boolean holdsLock(TransactionId tid, PageId pid) {
		// check for shared locks
         Set<TransactionId> listOfTIDs = sharedLocksOnPage.get(pid);
         
         if(listOfTIDs != null && listOfTIDs.contains(tid)){
                 return true;
         }
         
         //check for exclusive lock
         TransactionId exLock = exclusiveLockOnPage.get(pid);
         if(exLock != null && exLock.equals(tid)){
                 return true;
         }
         
         return false;
	 }
	 
	 public synchronized void releaseAllLocks(TransactionId tid) {
		 
		 Set<PageId> sharedLockedPages = sharedLocksOnPage.keySet();
		 for (PageId Spid : sharedLockedPages)
		 {
			 Set<TransactionId> TIDSet = sharedLocksOnPage.get(Spid);
             if(TIDSet != null)
             {
            	 TIDSet.remove(tid); 
                 sharedLocksOnPage.put(Spid, TIDSet);
             }
		 }
		 
		 Set<PageId> exclusiveLockedPages = exclusiveLockOnPage.keySet();
		 Set<PageId> copyExclusive = new HashSet<PageId>();
		 
		 for (PageId Epid : exclusiveLockedPages)
			 copyExclusive.add(Epid);
		 
		 for (PageId remove : copyExclusive)
		 {
			 TransactionId removeTID = exclusiveLockOnPage.get(remove);
             if(removeTID != null && removeTID.equals(tid))
                     exclusiveLockOnPage.remove(remove);
		 }
		 
	 }
}
	 
         
  

