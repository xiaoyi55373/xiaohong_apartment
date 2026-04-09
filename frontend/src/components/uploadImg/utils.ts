/**
 * 图片上传工具函数
 * @description 提供图片上传相关的工具函数，包括文件大小格式化、图片压缩等
 */

/**
 * 格式化文件大小
 * @param bytes 文件大小（字节）
 * @returns 格式化后的字符串
 */
export function formatFileSize(bytes: number): string {
  if (bytes === 0) return '0 B'
  
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

/**
 * 图片压缩选项
 */
export interface CompressOptions {
  quality?: number
  maxWidth?: number
  maxHeight?: number
  mimeType?: string
}

/**
 * 压缩图片
 * @param file 原始图片文件
 * @param quality 压缩质量 (0-1)
 * @param options 其他选项
 * @returns 压缩后的文件
 */
export function compressImage(
  file: File,
  quality: number = 0.8,
  options: CompressOptions = {}
): Promise<File> {
  return new Promise((resolve, reject) => {
    const {
      maxWidth = 1920,
      maxHeight = 1920,
      mimeType = file.type || 'image/jpeg'
    } = options

    // 创建图片对象
    const img = new Image()
    const url = URL.createObjectURL(file)
    
    img.onload = () => {
      URL.revokeObjectURL(url)
      
      // 计算压缩后的尺寸
      let { width, height } = img
      
      if (width > maxWidth || height > maxHeight) {
        const ratio = Math.min(maxWidth / width, maxHeight / height)
        width = Math.floor(width * ratio)
        height = Math.floor(height * ratio)
      }
      
      // 创建画布
      const canvas = document.createElement('canvas')
      canvas.width = width
      canvas.height = height
      
      const ctx = canvas.getContext('2d')
      if (!ctx) {
        reject(new Error('无法创建 canvas 上下文'))
        return
      }
      
      // 绘制图片
      ctx.fillStyle = '#FFFFFF'
      ctx.fillRect(0, 0, width, height)
      ctx.drawImage(img, 0, 0, width, height)
      
      // 转换为 Blob
      canvas.toBlob(
        (blob) => {
          if (!blob) {
            reject(new Error('图片压缩失败'))
            return
          }
          
          // 创建新文件
          const compressedFile = new File([blob], file.name, {
            type: mimeType,
            lastModified: Date.now()
          })
          
          resolve(compressedFile)
        },
        mimeType,
        quality
      )
    }
    
    img.onerror = () => {
      URL.revokeObjectURL(url)
      reject(new Error('图片加载失败'))
    }
    
    img.src = url
  })
}

/**
 * 获取图片尺寸
 * @param file 图片文件
 * @returns 图片宽高
 */
export function getImageSize(file: File): Promise<{ width: number; height: number }> {
  return new Promise((resolve, reject) => {
    const img = new Image()
    const url = URL.createObjectURL(file)
    
    img.onload = () => {
      URL.revokeObjectURL(url)
      resolve({
        width: img.width,
        height: img.height
      })
    }
    
    img.onerror = () => {
      URL.revokeObjectURL(url)
      reject(new Error('图片加载失败'))
    }
    
    img.src = url
  })
}

/**
 * 将文件转换为 Base64
 * @param file 文件对象
 * @returns Base64 字符串
 */
export function fileToBase64(file: File): Promise<string> {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    
    reader.onload = () => {
      resolve(reader.result as string)
    }
    
    reader.onerror = () => {
      reject(new Error('文件读取失败'))
    }
    
    reader.readAsDataURL(file)
  })
}

/**
 * 将 Base64 转换为文件
 * @param base64 Base64 字符串
 * @param filename 文件名
 * @returns 文件对象
 */
export function base64ToFile(base64: string, filename: string): File {
  const arr = base64.split(',')
  const mime = arr[0].match(/:(.*?);/)?.[1] || 'image/jpeg'
  const bstr = atob(arr[1])
  let n = bstr.length
  const u8arr = new Uint8Array(n)
  
  while (n--) {
    u8arr[n] = bstr.charCodeAt(n)
  }
  
  return new File([u8arr], filename, { type: mime })
}

/**
 * 检查文件是否为图片
 * @param file 文件对象
 * @returns 是否为图片
 */
export function isImage(file: File): boolean {
  return file.type.startsWith('image/')
}

/**
 * 获取文件扩展名
 * @param filename 文件名
 * @returns 扩展名（小写）
 */
export function getFileExtension(filename: string): string {
  return filename.split('.').pop()?.toLowerCase() || ''
}

/**
 * 生成唯一文件名
 * @param originalName 原始文件名
 * @returns 唯一文件名
 */
export function generateUniqueFileName(originalName: string): string {
  const ext = getFileExtension(originalName)
  const timestamp = Date.now()
  const random = Math.random().toString(36).substring(2, 8)
  return `${timestamp}_${random}.${ext}`
}

/**
 * 图片裁剪选项
 */
export interface CropOptions {
  x: number
  y: number
  width: number
  height: number
}

/**
 * 裁剪图片
 * @param file 图片文件
 * @param cropOptions 裁剪选项
 * @returns 裁剪后的文件
 */
export function cropImage(
  file: File,
  cropOptions: CropOptions
): Promise<File> {
  return new Promise((resolve, reject) => {
    const img = new Image()
    const url = URL.createObjectURL(file)
    
    img.onload = () => {
      URL.revokeObjectURL(url)
      
      const canvas = document.createElement('canvas')
      canvas.width = cropOptions.width
      canvas.height = cropOptions.height
      
      const ctx = canvas.getContext('2d')
      if (!ctx) {
        reject(new Error('无法创建 canvas 上下文'))
        return
      }
      
      ctx.drawImage(
        img,
        cropOptions.x,
        cropOptions.y,
        cropOptions.width,
        cropOptions.height,
        0,
        0,
        cropOptions.width,
        cropOptions.height
      )
      
      canvas.toBlob(
        (blob) => {
          if (!blob) {
            reject(new Error('图片裁剪失败'))
            return
          }
          
          const croppedFile = new File([blob], file.name, {
            type: file.type,
            lastModified: Date.now()
          })
          
          resolve(croppedFile)
        },
        file.type
      )
    }
    
    img.onerror = () => {
      URL.revokeObjectURL(url)
      reject(new Error('图片加载失败'))
    }
    
    img.src = url
  })
}

/**
 * 创建图片缩略图
 * @param file 图片文件
 * @param maxSize 最大尺寸
 * @returns 缩略图 Base64
 */
export function createThumbnail(
  file: File,
  maxSize: number = 200
): Promise<string> {
  return new Promise((resolve, reject) => {
    const img = new Image()
    const url = URL.createObjectURL(file)
    
    img.onload = () => {
      URL.revokeObjectURL(url)
      
      let { width, height } = img
      
      if (width > maxSize || height > maxSize) {
        const ratio = Math.min(maxSize / width, maxSize / height)
        width = Math.floor(width * ratio)
        height = Math.floor(height * ratio)
      }
      
      const canvas = document.createElement('canvas')
      canvas.width = width
      canvas.height = height
      
      const ctx = canvas.getContext('2d')
      if (!ctx) {
        reject(new Error('无法创建 canvas 上下文'))
        return
      }
      
      ctx.drawImage(img, 0, 0, width, height)
      
      resolve(canvas.toDataURL('image/jpeg', 0.7))
    }
    
    img.onerror = () => {
      URL.revokeObjectURL(url)
      reject(new Error('图片加载失败'))
    }
    
    img.src = url
  })
}

/**
 * 验证图片尺寸
 * @param file 图片文件
 * @param minWidth 最小宽度
 * @param minHeight 最小高度
 * @param maxWidth 最大宽度
 * @param maxHeight 最大高度
 * @returns 验证结果
 */
export async function validateImageSize(
  file: File,
  minWidth?: number,
  minHeight?: number,
  maxWidth?: number,
  maxHeight?: number
): Promise<{ valid: boolean; message?: string }> {
  try {
    const { width, height } = await getImageSize(file)
    
    if (minWidth && width < minWidth) {
      return { valid: false, message: `图片宽度不能小于 ${minWidth}px` }
    }
    
    if (minHeight && height < minHeight) {
      return { valid: false, message: `图片高度不能小于 ${minHeight}px` }
    }
    
    if (maxWidth && width > maxWidth) {
      return { valid: false, message: `图片宽度不能大于 ${maxWidth}px` }
    }
    
    if (maxHeight && height > maxHeight) {
      return { valid: false, message: `图片高度不能大于 ${maxHeight}px` }
    }
    
    return { valid: true }
  } catch (error) {
    return { valid: false, message: '无法读取图片尺寸' }
  }
}
