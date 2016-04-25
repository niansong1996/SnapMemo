package org.sensation.snapmemo.server.BusinessLogic.ABD;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Ploter {
	ArrayList<LineVO> bb = new ArrayList<LineVO>();
	public Ploter(ArrayList<LineVO> bb){
		this.bb = bb;
	}
	
	public void plotImage(){

		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		MyPanel panel = new MyPanel();
		frame.getContentPane().add(panel);

		frame.setSize(540, 960);
		frame.setVisible(true);
	}
	
	class MyPanel extends JPanel{
		int x;
		int y;
		int xlength;
		int ylength;

		@Override
		public void paintComponent(Graphics g){
			Color color = Color.white;
			g.setColor(color);
			g.fillRect(0, 0, 540, 960);

			color = Color.red;
			g.setColor(color);
			for(LineVO t : bb){
//				System.out.println(t);
				x = t.x/2;
				y = t.y/2;
				xlength = t.xlength/2;
				ylength = t.ylength/2;
				g.fillRect(x,y,xlength,ylength);
			}

		}
	}
}
