/*******************************************************************************
 * Copyright 2011 Beintoo
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.beintoo.beintoosdkutility;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

public class LayoutUtils {

	public enum Layout {
	  WidthFill_HeightFill,
	  WidthWrap_HeightWrap,
	  WidthWrap_HeightFill,
	  WidthFill_HeightWrap;

	  public void applyViewGroupParams(View component) {
	    applyViewGroupLayoutParamsTo(this, component);
	  }

	  public void applyLinearLayoutParams(View linearlayout) {
	    applyLinearLayoutParamsTo(this, linearlayout);
	  }

	  public void applyTableRowParams(View cell) {
	    applyTableRowLayoutParamsTo(this, cell);
	  }

	  public void applyTableLayoutParams(View row) {
	    applyTableLayoutParamsTo(this, row);
	  }

	}

	private static void applyLinearLayoutParamsTo(Layout layout, View view) {

	  switch (layout) {
	    case WidthFill_HeightFill:
	      view.setLayoutParams(new LinearLayout.LayoutParams(
	          LinearLayout.LayoutParams.FILL_PARENT,
	          LinearLayout.LayoutParams.FILL_PARENT
	      ));
	      break;
	    case WidthFill_HeightWrap:
	      view.setLayoutParams(new LinearLayout.LayoutParams(
	          LinearLayout.LayoutParams.FILL_PARENT,
	          LinearLayout.LayoutParams.WRAP_CONTENT
	      ));
	      break;
	    case WidthWrap_HeightFill:
	      view.setLayoutParams(new LinearLayout.LayoutParams(
	          LinearLayout.LayoutParams.WRAP_CONTENT,
	          LinearLayout.LayoutParams.FILL_PARENT
	      ));
	      break;
	    case WidthWrap_HeightWrap:
	      view.setLayoutParams(new LinearLayout.LayoutParams(
	          LinearLayout.LayoutParams.WRAP_CONTENT,
	          LinearLayout.LayoutParams.WRAP_CONTENT
	      ));
	      break;
	  }

	}

	private static void applyViewGroupLayoutParamsTo(Layout layout, View view) {

	  switch (layout) {
	    case WidthFill_HeightFill:
	      view.setLayoutParams(new ViewGroup.LayoutParams(
	          ViewGroup.LayoutParams.FILL_PARENT,
	          ViewGroup.LayoutParams.FILL_PARENT
	      ));
	      break;
	    case WidthFill_HeightWrap:
	      view.setLayoutParams(new ViewGroup.LayoutParams(
	          ViewGroup.LayoutParams.FILL_PARENT,
	          ViewGroup.LayoutParams.WRAP_CONTENT
	      ));
	      break;
	    case WidthWrap_HeightFill:
	      view.setLayoutParams(new ViewGroup.LayoutParams(
	          ViewGroup.LayoutParams.WRAP_CONTENT,
	          ViewGroup.LayoutParams.FILL_PARENT
	      ));
	      break;
	    case WidthWrap_HeightWrap:
	      view.setLayoutParams(new ViewGroup.LayoutParams(
	          ViewGroup.LayoutParams.WRAP_CONTENT,
	          ViewGroup.LayoutParams.WRAP_CONTENT
	      ));
	      break;
	  }

	}

	private static void applyTableRowLayoutParamsTo(Layout layout, View view) {

	  switch (layout) {
	    case WidthFill_HeightFill:
	      view.setLayoutParams(new TableRow.LayoutParams(
	          TableRow.LayoutParams.FILL_PARENT,
	          TableRow.LayoutParams.FILL_PARENT
	      ));
	      break;
	    case WidthFill_HeightWrap:
	      view.setLayoutParams(new TableRow.LayoutParams(
	          TableRow.LayoutParams.FILL_PARENT,
	          TableRow.LayoutParams.WRAP_CONTENT
	      ));
	      break;
	    case WidthWrap_HeightFill:
	      view.setLayoutParams(new TableRow.LayoutParams(
	          TableRow.LayoutParams.WRAP_CONTENT,
	          TableRow.LayoutParams.FILL_PARENT
	      ));
	      break;
	    case WidthWrap_HeightWrap:
	      view.setLayoutParams(new TableRow.LayoutParams(
	          TableRow.LayoutParams.WRAP_CONTENT,
	          TableRow.LayoutParams.WRAP_CONTENT
	      ));
	      break;
	  }

	}

	private static void applyTableLayoutParamsTo(Layout layout, View view) {

	  switch (layout) {
	    case WidthFill_HeightFill:
	      view.setLayoutParams(new TableLayout.LayoutParams(
	          TableLayout.LayoutParams.FILL_PARENT,
	          TableLayout.LayoutParams.FILL_PARENT
	      ));
	      break;
	    case WidthFill_HeightWrap:
	      view.setLayoutParams(new TableLayout.LayoutParams(
	          TableLayout.LayoutParams.FILL_PARENT,
	          TableLayout.LayoutParams.WRAP_CONTENT
	      ));
	      break;
	    case WidthWrap_HeightFill:
	      view.setLayoutParams(new TableLayout.LayoutParams(
	          TableLayout.LayoutParams.WRAP_CONTENT,
	          TableLayout.LayoutParams.FILL_PARENT
	      ));
	      break;
	    case WidthWrap_HeightWrap:
	      view.setLayoutParams(new TableLayout.LayoutParams(
	          TableLayout.LayoutParams.WRAP_CONTENT,
	          TableLayout.LayoutParams.WRAP_CONTENT
	      ));
	      break;
	  }

	}

	}//end class LayoutUtils
