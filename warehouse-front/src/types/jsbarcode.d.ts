declare module 'jsbarcode' {
  interface JsBarcodeOptions {
    format?: string
    width?: number
    height?: number
    displayValue?: boolean
    fontSize?: number
    margin?: number
    textMargin?: number
    lineColor?: string
    background?: string
  }
  function JsBarcode(element: SVGElement | HTMLCanvasElement | HTMLImageElement, value: string, options?: JsBarcodeOptions): void
  export default JsBarcode
}
