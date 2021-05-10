package com.parkit.parkingsystem.service;

import java.util.Date;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

	public void calculateFare(Ticket ticket) {
		if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
			throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
		}

		Date inTime = ticket.getInTime();
		Date outTime = ticket.getOutTime();

		// TODO: Some tests are failing here. Need to check if this logic is correct
		double duration = ((outTime.getTime() - inTime.getTime()) / ((double) 60 * 60 * 1000));
		//double freeFareDuration = (double)( 30 * 60 * 1000) / (60 * 60 * 1000);
		
		// 30min free feature (If 30minOrLess>free else switch)
		
		switch (ticket.getParkingSpot().getParkingType()) {
		case CAR: {
			/* if (duration <= freeFareDuration) {
				ticket.setPrice(Fare.FREE_FARE);	
				break;
			} elseif (duration > freeFareDuration) { */
				ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR);
				break;
			//}
			
			
		}
		case BIKE: {
			/*if (duration <= freeFareDuration) {
				ticket.setPrice(Fare.FREE_FARE);
				break;
			} else if (duration > freeFareDuration) { */
				ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR);	
				break;
			//}
		
		}
		default:
			throw new IllegalArgumentException("Unkown Parking Type");
		}

	}
}