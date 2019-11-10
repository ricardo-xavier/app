	package xavier.ricardo.softapp;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class DrawView extends View {
	
	private static final int HEIGHT = 200;
	
	private List<List<Ponto>> partes = new ArrayList<List<Ponto>>();
	private List<Ponto> pontos = new ArrayList<Ponto>();
	
	public DrawView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
        setOnTouchListener(new OnTouchListener() { 
        	@Override 
        	public boolean onTouch(View view, MotionEvent event) { 
        		switch (event.getAction()) {
        		
        		case 0: // aperta
        			Ponto p = new Ponto((int) event.getX(), (int) event.getY());
        			pontos.add(p);
        			partes.add(pontos);
        			break;
        			
        		case 1: // solta
        			p = new Ponto((int) event.getX(), (int) event.getY());
        			pontos.add(p);
        			pontos = new ArrayList<Ponto>();
        			invalidate();
        			break;
        			
        		case 2: // move
        			p = new Ponto((int) event.getX(), (int) event.getY());
        			pontos.add(p);
        			invalidate();
        			break;
        		}
        		return true; 
        	}
        });
		
	}

	@Override
	protected void onDraw(Canvas canvas) {
		
		Paint paint = new Paint();
		paint.setColor(Color.rgb(200, 200, 200));
		paint.setStyle(Paint.Style.FILL);
		canvas.drawRect(0, 0, this.getWidth(), this.getHeight(), paint);
		
		if (partes.size() == 0) {
			return;
		}
		paint.setColor(Color.BLACK);
		paint.setStrokeWidth(3);
		for (List<Ponto> pontos : partes) {
			if (pontos.size() < 2) {
				continue;
			}
			for (int i=1; i<pontos.size(); i++) {
				Ponto u = pontos.get(i-1);
				Ponto v = pontos.get(i);
				canvas.drawLine(u.getX(), u.getY(),	v.getX(), v.getY(), paint);
			}
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
		setMeasuredDimension(parentWidth, HEIGHT);
		
	}

	public void limpa() {
		partes.clear();
		invalidate();
	}

}
