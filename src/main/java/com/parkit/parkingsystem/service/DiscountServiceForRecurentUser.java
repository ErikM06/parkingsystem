package com.parkit.parkingsystem.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.model.Ticket;

public class DiscountServiceForRecurentUser {

	private DataBaseConfig _dataBaseConfig = new DataBaseConfig();

	public void checkRightForFivePercentDiscount(Ticket ticket) {
		Connection con = null;
		ResultSet rs = null;
		String readerRegNumber = null;
		String canGetTheDiscount = null;
		int discount = 0;

		try {
			con = _dataBaseConfig.getConnection();

			readerRegNumber = ticket.getVehicleRegNumber();

			PreparedStatement ps = con.prepareStatement(
					"SELECT VEHICLE_REG_NUMBER FROM prod.ticket WHERE VEHICLE_REG_NUMBER = ? AND OUT_TIME IS NOT NULL LIMIT 1");
			ps.setString(1, readerRegNumber);
			rs = ps.executeQuery();
			if (rs != null) {
				while (rs.next()) {
					canGetTheDiscount = rs.getString(1);
					if (canGetTheDiscount.equals(readerRegNumber)) {
						discount = 1;
						ticket.setGetDiscount(discount);
						System.out.println(
								"Welcome back!  As a recurring user of our parking lot, you'll benefit from a 5% discount.");
					}
				}
			} else {
				discount = 0;
				ticket.setGetDiscount(discount);
			}

			_dataBaseConfig.closeConnection(con);
		}
	  catch (RuntimeException e) {
		    throw e;
		  } 
		catch (Exception e) {

		}
	}
	
	public void applyFivePercentDiscount(Ticket ticket) {
		double priceWithoutDiscount = 0;
		double priceWithDiscount = 0;
		int applyDiscount = 0;

		applyDiscount = ticket.getDiscount();
		if (applyDiscount >= 1) {
			priceWithoutDiscount = ticket.getPrice();
			System.out.println("Price without discount is: " + priceWithoutDiscount);

			priceWithDiscount = priceWithoutDiscount - ((priceWithoutDiscount * 5) / 100);
			ticket.setPrice(priceWithDiscount);
			System.out.println("Price with discount is: " + priceWithDiscount);

		} else {	
			ticket.getPrice();
		}
	}

}
