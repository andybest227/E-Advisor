public class CustomDialogClass extends Dialog implements View.OnClickListener {
    public Activity c;
    public  Dialog d;
    public Button yes, no;

    public CustomDialogClass(Activity activity){
        super(activity);
        this.c = activity;
    }
    @Override
    protected  void  onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog);
        yes = findViewById(R.id.btn_yes);
        no = findViewById(R.id.btn_no);
        yes.setOnClickListener(this);
        no.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_yes:
                c.finish();
                break;
            case R.id.btn_no:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }
}
