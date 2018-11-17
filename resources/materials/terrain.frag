
#ifdef HAS_COLORMAP
  uniform sampler2D m_ColorMap;
#endif
#ifdef HAS_WATERCOLORMAP
  uniform sampler2D m_WaterColorMap;
#endif
#ifdef HAS_WATER_COLOR
  uniform vec4 m_WaterColor;
#endif
#ifdef RENDER_COASTLINE
  uniform bool m_RenderCoastline;
  #ifdef HAS_COASTLINE_THICKNESS
    uniform float m_CoastlineThickness;
  #endif
#endif

varying vec2 texCoord1;
varying float intensity;
varying float isWater;

void main(){

    vec4 color = vec4(1.0);

    #ifdef HAS_COLORMAP
      if(isWater != 1.0) {
        color *= texture2D(m_ColorMap, texCoord1);
      }     
    #endif
    
   
    
    #ifdef HAS_WATERCOLORMAP
      vec4 wcol  = texture2D(m_WaterColorMap, texCoord1);
    #else
      #ifdef HAS_WATER_COLOR
        vec4 wcol = m_WaterColor; 
      #else
        vec4 wcol = vec4(0.0,0.0,0.7,1.0);
      #endif                        
    #endif

    color = mix(wcol, color, 1.0 - isWater);
    color *= intensity;
        
    #ifdef RENDER_COASTLINE        
      if(m_RenderCoastline) {                  
         #ifdef HAS_COASTLINE_THICKNESS
           float thinness = 1.0 - m_CoastlineThickness;
         #else
           float thinness = 0.001;           
         #endif  
         thinness = clamp(thinness, 0.001, 0.999);       
         if(isWater < 1.0 - thinness && isWater > thinness) {
           color = vec4(0.0,0.0,0.0,1.0);
         } 
         //else { discard; }
      }    
    #endif
    
    //float gs = (color.r + color.g + color.b)/3.0;
    //color = vec4(gs,gs,gs,1.0);
    //color = vec4(1.0 - gs,1.0 - gs,1.0 - gs,1.0);
    
    
    color.w = 1.0;

    gl_FragColor = color;
}