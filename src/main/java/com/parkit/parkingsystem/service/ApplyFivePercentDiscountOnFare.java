package com.parkit.parkingsystem.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.InputReaderUtil;

public class ApplyFivePercentDiscountOnFare {


	private InputReaderUtil inputReaderUtil;
	private ParkingSpotDAO parkingSpotDAO;
	private TicketDAO ticketDAO;
	private Ticket _ticket = new Ticket();
	private DataBaseConfig _dataBaseConfig = new DataBaseConfig();

	public void checkRightForFivePercentDiscount() {
		Connection con = null;
		ResultSet rs = null;
		String readerRegNumber = null;
		String canGetTheDiscount = null;
		Boolean discount = null;

		try {
			con = _dataBaseConfig.getConnection();
			readerRegNumber = _ticket.getVehicleRegNumber();

			PreparedStatement ps = con.prepareStatement(
					"SELECT VEHICLE_REG_NUMBER FROM ticket WHERE VEHICLE_REG_NUMBER = ? AND OUT_TIME IS NOT NULL");
			ps.setString(1, readerRegNumber);
			rs = ps.executeQuery();
			if (rs.next()) {
				canGetTheDiscount = rs.getString(1);
				System.out.println(canGetTheDiscount);
			}
			if (canGetTheDiscount.equals(readerRegNumber)) {
				discount = true;
				_ticket.setCanGetDiscount(discount);
				System.out.println(
						"Welcome back!  As a recurring user of our parking lot, you'll benefit from a 5% discount.");
				

			} else {
				discount = false;
				_ticket.setCanGetDiscount(discount);
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

	public void applyFivePercentDiscount() {
		double priceWithoutDiscount = 0;
		double priceWithDiscount = 0;
		Boolean applyDiscount;
		
		applyDiscount = _ticket.canGetDiscount();
		if (applyDiscount == true) {
			priceWithoutDiscount = _ticket.getPrice();
			priceWithDiscount = priceWithoutDiscount * (5 / 100);
			_ticket.setPrice(priceWithDiscount);
		} else {
			_ticket.getPrice();
		}

	}
}
