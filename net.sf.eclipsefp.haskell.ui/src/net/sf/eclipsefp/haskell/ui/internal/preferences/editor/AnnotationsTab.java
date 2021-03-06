// Copyright (c) 2003-2005 by Leif Frenzel - see http://leiffrenzel.de
package net.sf.eclipsefp.haskell.ui.internal.preferences.editor;

import java.util.ArrayList;
import java.util.Iterator;
import net.sf.eclipsefp.common.ui.preferences.Tab;
import net.sf.eclipsefp.common.ui.preferences.overlay.OverlayPreferenceStore;
import net.sf.eclipsefp.common.ui.util.DialogUtil;
import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.texteditor.AnnotationPreference;
import org.eclipse.ui.texteditor.MarkerAnnotationPreferences;


/** <p>Tab for the annotation color preference settings.</p>
  *
  * @author Leif Frenzel
  */
class AnnotationsTab extends Tab {

  private List colorList;

  private final String[][] colorListModel;
  private ColorSelector colorSelector;
  private Button cbShowInText;
  private Button cbShowInOverviewRuler;


  AnnotationsTab( final IPreferenceStore store ) {
    super( store );
    colorListModel = createAnnotationTypeListModel();
  }


  // interface methods of Tab
  ///////////////////////////

  @Override
  public Control createControl( final Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    GridLayout layout = new GridLayout();
    layout.numColumns = 2;
    composite.setLayout( layout );

    initializeAnnPresLabel( composite );
    Composite editorComposite = initializeEditorComposite( composite );
    initializeColorList( composite, editorComposite );
    Composite optionsComposite = initializeOptionsComposite( editorComposite );
    initializeShowInTextCB( optionsComposite );
    initializeShowInOverviewRulerCB( optionsComposite );
    createLabel( optionsComposite, "C&olor:" );
    initializeColorSelector( optionsComposite );

    initialize();

    return composite;
  }

  void addPreferences( final OverlayPreferenceStore store ) {
    MarkerAnnotationPreferences preferences = new MarkerAnnotationPreferences();
    Iterator iter = preferences.getAnnotationPreferences().iterator();
    while( iter.hasNext() ) {
      AnnotationPreference info = ( AnnotationPreference )iter.next();
      store.addStringKey( info.getColorPreferenceKey() );
      store.addBooleanKey( info.getTextPreferenceKey() );
      store.addBooleanKey( info.getOverviewRulerPreferenceKey() );
    }
  }


  // UI initialization methods
  ////////////////////////////

  private Composite initializeOptionsComposite( final Composite parent ) {
    Composite optionsComposite = new Composite( parent, SWT.NONE );
    GridLayout layout = new GridLayout();
    layout.marginHeight = 0;
    layout.marginWidth = 0;
    layout.numColumns = 2;
    optionsComposite.setLayout( layout );
    optionsComposite.setLayoutData( new GridData( GridData.FILL_BOTH ) );
    return optionsComposite;
  }

  private void initializeColorList( final Composite composite,
                                    final Composite parent ) {
    colorList = new List( parent, SWT.SINGLE | SWT.V_SCROLL | SWT.BORDER );
    GridData gridData = new GridData(   GridData.VERTICAL_ALIGN_BEGINNING
                                      | GridData.FILL_HORIZONTAL );
    gridData.heightHint = DialogUtil.convertHeightInCharsToPixels( composite,
                                                                   8 );
    colorList.setLayoutData( gridData );
    colorList.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent e ) {
        handleAnnotationColorListSelection();
      }
    } );
  }

  private void initializeColorSelector( final Composite parent ) {
    colorSelector = new ColorSelector( parent );
    Button foregroundColorButton = colorSelector.getButton();
    GridData gridData = new GridData( GridData.FILL_HORIZONTAL );
    gridData.horizontalAlignment = GridData.BEGINNING;
    foregroundColorButton.setLayoutData( gridData );
    foregroundColorButton.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent e ) {
        int i = colorList.getSelectionIndex();
        String key = colorListModel[ i ][ 1 ];
        RGB colorValue = colorSelector.getColorValue();
        PreferenceConverter.setValue( getPreferenceStore(), key, colorValue );
      }
    } );
  }

  private void initializeAnnPresLabel( final Composite parent ) {
    Label label = new Label( parent, SWT.LEFT );
    label.setText( "Annotation &presentation:" );
    GridData gridData = new GridData( GridData.HORIZONTAL_ALIGN_FILL );
    gridData.horizontalSpan = 2;
    label.setLayoutData( gridData );
  }

  private Composite initializeEditorComposite( final Composite parent ) {
    Composite editorComposite = new Composite( parent, SWT.NONE );
    GridLayout layout = new GridLayout();
    layout.numColumns = 2;
    layout.marginHeight = 0;
    layout.marginWidth = 0;
    editorComposite.setLayout( layout );
    GridData gridData = new GridData(   GridData.HORIZONTAL_ALIGN_FILL
                                      | GridData.FILL_VERTICAL );
    gridData.horizontalSpan = 2;
    editorComposite.setLayoutData( gridData );
    return editorComposite;
  }

  private void initializeShowInOverviewRulerCB( final Composite parent ) {
    cbShowInOverviewRuler = new Button( parent, SWT.CHECK );
    cbShowInOverviewRuler.setText( "Show in overview &ruler" );
    GridData gridData = new GridData( GridData.FILL_HORIZONTAL );
    gridData.horizontalAlignment = GridData.BEGINNING;
    gridData.horizontalSpan = 2;
    cbShowInOverviewRuler.setLayoutData( gridData );
    cbShowInOverviewRuler.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent e ) {
        int i = colorList.getSelectionIndex();
        String key = colorListModel[ i ][ 3 ];
        boolean selected = cbShowInOverviewRuler.getSelection();
        getPreferenceStore().setValue( key, selected );
      }
    } );
  }

  private void initializeShowInTextCB( final Composite parent ) {
    cbShowInText = new Button( parent, SWT.CHECK );
    cbShowInText.setText( "Show in &text" );
    GridData gridData = new GridData( GridData.FILL_HORIZONTAL );
    gridData.horizontalAlignment = GridData.BEGINNING;
    gridData.horizontalSpan = 2;
    cbShowInText.setLayoutData( gridData );
    cbShowInText.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent e ) {
        int i = colorList.getSelectionIndex();
        String key = colorListModel[ i ][ 2 ];
        boolean selected = cbShowInText.getSelection();
        getPreferenceStore().setValue( key, selected );
      }
    } );
  }


  // helping methods
  //////////////////

  private void handleAnnotationColorListSelection() {
    int i = colorList.getSelectionIndex();
    String key = colorListModel[ i ][ 1 ];
    RGB rgb = PreferenceConverter.getColor( getPreferenceStore(), key );
    colorSelector.setColorValue( rgb );
    key = colorListModel[ i ][ 2 ];
    boolean sel = getPreferenceStore().getBoolean( key );
    cbShowInText.setSelection( sel );
    key = colorListModel[ i ][ 3 ];
    cbShowInOverviewRuler.setSelection( sel );
  }

  private String[][] createAnnotationTypeListModel() {
    MarkerAnnotationPreferences preferences = new MarkerAnnotationPreferences();
    ArrayList<String[]> listModelItems = new ArrayList<String[]>();
    Iterator i = preferences.getAnnotationPreferences().iterator();
    while( i.hasNext() ) {
      AnnotationPreference info = ( AnnotationPreference )i.next();
      listModelItems.add( new String[] {
        info.getPreferenceLabel(),
        info.getColorPreferenceKey(),
        info.getTextPreferenceKey(),
        info.getOverviewRulerPreferenceKey() }
      );
    }

    String[][] listModel = new String[ listModelItems.size() ][];
    listModelItems.toArray( listModel );
    return listModel;
  }

  private void initialize() {
    for( int i = 0; i < colorListModel.length; i++ ) {
      String color = colorListModel[ i ][ 0 ];
      if( color != null ) {
        colorList.add( color );
      }
    }
    colorList.getDisplay().asyncExec( new Runnable() {
      public void run() {
        if( ( colorList != null ) && !colorList.isDisposed() ) {
          colorList.select( 0 );
          handleAnnotationColorListSelection();
        }
      }
    } );
    initializeFields();
  }
}