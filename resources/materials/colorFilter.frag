
uniform sampler2D m_Texture;

#ifdef BLACK_AND_WHITE
  uniform bool m_IsBlackAndWhite;
#endif
#ifdef SEPIA
  uniform bool m_IsSepia;
#endif
#ifdef INVERT_COLORS
  uniform bool m_IsInvertColors;
#endif
#ifdef BRIGHTNESS
  uniform float m_Brightness;
#endif
#ifdef CONTRAST
  uniform float m_Contrast;
#endif

varying vec2 texCoord;

void main() {
   
   vec4 color = texture2D(m_Texture, texCoord);
   
   #ifdef BLACK_AND_WHITE
     if(m_IsBlackAndWhite) {
       float val = (color.r + color.g + color.b) / 3.0;
       color = vec4(val,val,val,color.w);
     }   
   #endif
   
   #ifdef INVERT_COLORS
     if(m_IsInvertColors) {
       
       color = vec4(1.0 - color.r, 1.0 - color.g, 1.0 - color.b, 1.0 - color.w);
     }   
   #endif  
   
   #ifdef SEPIA
     if(m_IsSepia) {
       float tr = 0.393 * color.r  + 0.769 * color.g + 0.189 * color.b;
       float tg = 0.349 * color.r + 0.686 * color.g + 0.168 * color.b;
       float tb = 0.272 * color.r + 0.534 * color.g + 0.131 * color.b;   
       color = vec4(clamp(tr,0,1), clamp(tg,0,1),clamp(tb,0,1), color.a);
     }
   #endif
   
   #ifdef BRIGHTNESS     
     //float bAdjust = m_Brightness - 0.5;
     float bAdjust = m_Brightness;
     color = vec4(clamp(color.r + bAdjust, 0, 1),clamp(color.g + bAdjust, 0, 1),clamp(color.b + bAdjust, 0, 1),color.w);        
   #endif      
   
   #ifdef CONTRAST
     float conScaled = 255.0 * m_Contrast;
     float cFact = (259.0 * (conScaled + 255.0)) / (255.0 * (259.0 - conScaled));
     float r = (cFact * (color.r - 0.5)) + 0.5; 
     float g = (cFact * (color.g - 0.5)) + 0.5;
     float b = (cFact * (color.b - 0.5)) + 0.5;
     
     color = vec4(r,g,b,color.w);
   #endif
   
   gl_FragColor = color;
}