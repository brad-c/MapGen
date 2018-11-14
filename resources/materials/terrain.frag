
uniform sampler2D m_ColorMap;
uniform vec4 m_WaterColor;

varying vec2 texCoord1;
varying float intensity;
varying float isWater;

void main(){

    vec4 color = vec4(1.0);

    #ifdef HAS_COLORMAP
        color *= texture2D(m_ColorMap, texCoord1);     
    #endif

    if(isWater > 0.9) {
      #ifdef HAS_WATER_COLOR
        vec4 wcol = m_WaterColor; 
      #else
        vec4 wcol = vec4(0.0,0.0,0.7,1.0);
      #endif
      color = mix(wcol, color, 1.0 - isWater) ;
    }
    
    //lighting
    color *= intensity;

    gl_FragColor = color;
}