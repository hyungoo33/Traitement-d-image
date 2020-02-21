# pragma version (1)
# pragma rs java_package_name ( com.qps.projettp1)
# pragma rs fp relaxed



rs_allocation in;

int imageWidth;
int imageHeight;

int filterSize = 3;


void root(const uchar4* in, uchar4* out,uint32_t x, uint32_t y){

    float4 moy = 0;
    const uchar4* kin = in - filterSize*(imageWidth+1)/2;
    for(int i = 0; i < filterSize; i++){
        for(int j = 0; j < filterSize; j++){

            moy += rsUnpackColor8888( kin[i*imageWidth + j]);
        }
    }
    moy /= 9.0f;
    moy.a = 1.0f;

    *out = rsPackColorTo8888(moy);


}

void init(){

}

void setup(){

    imageWidth = rsAllocationGetDimX(in);
    imageHeight = rsAllocationGetDimY(in);
}
