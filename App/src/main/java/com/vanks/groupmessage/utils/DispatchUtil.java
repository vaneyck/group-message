package com.vanks.groupmessage.utils;

import com.vanks.groupmessage.enums.MessageStatus;
import com.vanks.groupmessage.models.persisted.Dispatch;

import java.util.List;

/**
 * Created by vaneyck on 11/21/15.
 */
public class DispatchUtil {
	public static int sentCount(List<Dispatch> dispatchList) {
		int count = 0;
		for (Dispatch dispatch : dispatchList) {
			if(dispatch.getStatus() == MessageStatus.SENT) {
				count++;
			}
		}
		return count;
	}
}
