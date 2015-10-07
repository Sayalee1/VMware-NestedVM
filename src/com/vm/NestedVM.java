package com.vm;
import java.net.MalformedURLException;
import java.net.URL; 
import java.rmi.RemoteException;
import java.util.Scanner;

import com.vmware.vim25.*;
import com.vmware.vim25.mo.*; 

public class NestedVM {
	public static void hostInfo(Folder rootFolder){
		int hostNo=0; 
		int datastoreNo= 0;
		int networkNo = 0;
		try {
			ManagedEntity[] mes = new InventoryNavigator(rootFolder).searchManagedEntities("HostSystem");
			if (mes == null || mes.length == 0) {    return;   }  
			  for (ManagedEntity hm: mes)
			  {
				  HostSystem host = (HostSystem)hm;
				  System.out.println("host[" + hostNo++ +"]:");
				  System.out.println("\tName = "+ host.getName());
				  System.out.println("\tProduct Full Name = "+ host.getConfig().getProduct().getFullName());
				  
				  Datastore[] ds = host.getDatastores();
				  for(Datastore vmds: ds){
					  System.out.println("\tDatastore[" + datastoreNo++ +"]:"+ "name:" + vmds.getName() + "," + "capacity:" + vmds.getSummary().capacity + "," + "freespace:" + vmds.getSummary().freeSpace );
				  }
				  Network[] net = host.getNetworks();
				  for(Network vmnet: net){
					  System.out.println("\tNetwork[" + networkNo++ +"]:" + "name:" + vmnet.getName());
				  }
			  }	  
			 
		} catch (RemoteException e) {
			e.printStackTrace();
		} 
	}
	public static void vmInfo(Folder rootFolder) throws InvalidProperty, RuntimeFault, RemoteException, MalformedURLException, InterruptedException{
		ManagedEntity[] mes1 = new InventoryNavigator(rootFolder).searchManagedEntities("HostSystem");
		ManagedEntity[] mes = new InventoryNavigator(rootFolder).searchManagedEntities("VirtualMachine");   
		  for (ManagedEntity hm1: mes1)
		  {
			  HostSystem hs1 = (HostSystem) hm1;
		int vmNo = 0;
		if (mes == null || mes.length == 0) {    return;   }
		for(ManagedEntity me: mes){
			VirtualMachine vm = (VirtualMachine) me; 
			System.out.println("VM[" + vmNo++ +"]:");
			System.out.println("\tName:" + vm.getName());    
			System.out.println("\tGuestOS: " + vm.getConfig().getGuestFullName()); 
			System.out.println("\tGuest State:"+ vm.getGuest().getGuestState());
			System.out.println("\tPower State:"+vm.getRuntime().getPowerState());
			if(vm.getName().equals("ubuntu1404-272-1"))
			{
				if(vm.getGuest().getGuestState().equals("running")){
					vm.powerOffVM_Task();
					System.out.println("\tPower off VM:");
					Task[] tasks = vm.getRecentTasks();
					for(Task t : tasks){
						t.waitForTask();
						System.out.println("\t\tstatus:"+ t.getTaskInfo().state);					
						System.out.println("\tTask: target:" +  vm.getName() + ", op: " + t.getTaskInfo().getName() + ", startTime:"+t.getTaskInfo().getStartTime().getTime());							
					}	
				}
				else{
					vm.powerOnVM_Task(hs1);
					System.out.println("\tPower on VM:");
					Task[] tasks = vm.getRecentTasks();
					for(Task t : tasks){
						t.waitForTask();
						System.out.println("\t\tstatus:"+ t.getTaskInfo().state);					
						System.out.println("\tTask: target:" +  vm.getName() + ", op: " + t.getTaskInfo().getName() + ", startTime:"+t.getTaskInfo().getStartTime().getTime());											
					
					}
				}	
				}			
				else if(vm.getName().equals("ubuntu1404-272-2")) 
				{
					if(vm.getRuntime().getPowerState().equals(VirtualMachinePowerState.poweredOn)){
						vm.powerOffVM_Task();
						System.out.println("\tPower off VM:");
						Task[] tasks = vm.getRecentTasks();
						for(Task t : tasks){
							t.waitForTask();
							System.out.println("\t\tstatus:"+ t.getTaskInfo().state);					
							System.out.println("\tTask: target:" +  vm.getName() + ", op: " + t.getTaskInfo().getName() + ", startTime:"+t.getTaskInfo().getStartTime().getTime());							
						}	
					}
					else{
						vm.powerOnVM_Task(hs1);
						System.out.println("\tPower on VM:");
						Task[] tasks = vm.getRecentTasks();
						for(Task t : tasks){
							t.waitForTask();
							System.out.println("\t\tstatus:"+ t.getTaskInfo().state);					
							System.out.println("\tTask: target:" +  vm.getName() + ", op: " + t.getTaskInfo().getName() + ", startTime:"+t.getTaskInfo().getStartTime().getTime());											
						
						}
					}	
					}		
		}
	 } 
	}
	public static void main(String[] args) throws InterruptedException {
		ServiceInstance si = null;
		try {
			
			Scanner inputone = new Scanner(System.in);
		    System.out.print("Enter the url: ");
		    String url = inputone.next();
			
			Scanner inputtwo = new Scanner(System.in);
		    System.out.print("Username: ");
		    String username = inputtwo.next();
		    
		    Scanner inputthree = new Scanner(System.in);
		    System.out.print("Password: ");
		    String password = inputthree.next();
		    si = new ServiceInstance(new URL(url), username, password, true);
			Folder rootFolder = si.getRootFolder(); 
			hostInfo(rootFolder);
			vmInfo(rootFolder);
		} catch (RemoteException | MalformedURLException e) {
			e.printStackTrace();
		} 
		finally
		{
			si.getServerConnection().logout();
		}

	}

}
