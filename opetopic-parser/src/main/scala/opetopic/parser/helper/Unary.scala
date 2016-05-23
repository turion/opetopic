package opetopic.parser.helper

object Unary {

  lazy val names = combining.keys ++ styles.keys ++ Set("\\not", "_", "^")

  def translate(command: String, param: String): String = {
    (command, param.trim) match {
      case ("\\^", "") => "^"
      case ("\\~", "") => "~"
      case (_, "") => ""
      case ("_", _) =>
        param.map(c => subscripts.getOrElse(c, c))
      case ("^", _) =>
        param.map(c => superscripts.getOrElse(c, c))
      case ("\\not", _) =>
        not.getOrElse(param.head, "¬" + param.head) + param.tail
      case _ =>
        if (combining.contains(command))
          param.head + combining(command) + param.tail
        else if (styles.contains(command)) {
          val map = styles(command)
          param.map(c => map.getOrElse(c, c.toString)).mkString
        }
        else ""
    }
  }

  val combining = Map(
    "\\hat" -> "\u0302",
    "\\^" -> "\u0302",
    "\\breve" -> "\u0306",
    "\\u" -> "\u0306",
    "\\grave" -> "\u0300",
    "\\`" -> "\u0300",
    "\\bar" -> "\u0304",
    "\\=" -> "\u0304",
    "\\check" -> "\u030C",
    "\\v" -> "\u030C",
    "\\acute" -> "\u0301",
    "\\'" -> "\u0301",
    "\\tilde" -> "\u0303",
    "\\~" -> "\u0303",
    "\\vec" -> "\u20D7",
    "\\dot" -> "\u0307",
    "\\." -> "\u0307",
    "\\ddot" -> "\u0308",
    "\\\"" -> "\u0308",
    "\\mathring" -> "\u030A",
    "\\H" -> "\u030B",
    "\\c" -> "\u0327",
    "\\k" -> "\u0328",
    "\\b" -> "\u0332",
    "\\d" -> "\u0323",
    "\\r" -> "\u030A",
    "\\t" -> "\u0361"
  )

  val not = Map(
    '∃' -> '∄',
    '∈' -> '∉',
    '∋' -> '∌',
    '⊂' -> '⊄',
    '⊃' -> '⊅',
    '⊆' -> '⊈',
    '⊇' -> '⊉',
    '≃' -> '≄',
    '∣' -> '∤',
    '∥' -> '∦',
    '=' -> '≠',
    '≈' -> '≉',
    '≡' -> '≢',
    '<' -> '≮',
    '>' -> '≯',
    '≤' -> '≰',
    '≥' -> '≱',
    '≲' -> '≴',
    '≳' -> '≵',
    '≶' -> '≸',
    '≷' -> '≹',
    '∼' -> '≁',
    '~' -> '≁',
    '≃' -> '≄',
    '⊒' -> '⋣',
    '⊑' -> '⋢',
    '⊴' -> '⋬',
    '⊵' -> '⋭',
    '◁' -> '⋪',
    '▷' -> '⋫',
    '⋞' -> '⋠',
    '⋟' -> '⋡'
  )

  val subscripts = Map(
    'χ' -> 'ᵪ',
    'φ' -> 'ᵩ',
    'ρ' -> 'ᵨ',
    'γ' -> 'ᵧ',
    'β' -> 'ᵦ',
    'x' -> 'ₓ',
    'v' -> 'ᵥ',
    'u' -> 'ᵤ',
    'r' -> 'ᵣ',
    'o' -> 'ₒ',
    'i' -> 'ᵢ',
	'j' -> 'ⱼ',
    'e' -> 'ₑ',
    'a' -> 'ₐ',
    '=' -> '₌',
    '9' -> '₉',
    '8' -> '₈',
    '7' -> '₇',
    '6' -> '₆',
    '5' -> '₅',
    '4' -> '₄',
    '3' -> '₃',
    '2' -> '₂',
    '1' -> '₁',
    '0' -> '₀',
    '-' -> '₋',
    '+' -> '₊',
    ')' -> '₎',
    '(' -> '₍'
  )

  val superscripts = Map(
    '∊' -> 'ᵋ',
    'χ' -> 'ᵡ',
    'φ' -> 'ᵠ',
    'ι' -> 'ᶥ',
    'θ' -> 'ᶿ',
    'δ' -> 'ᵟ',
    'γ' -> 'ᵞ',
    'β' -> 'ᵝ',
    'α' -> 'ᵅ',
    'Φ' -> 'ᶲ',
    'z' -> 'ᶻ',
    'y' -> 'ʸ',
    'x' -> 'ˣ',
    'w' -> 'ʷ',
    'v' -> 'ᵛ',
    'u' -> 'ᵘ',
    't' -> 'ᵗ',
    's' -> 'ˢ',
    'r' -> 'ʳ',
    'p' -> 'ᵖ',
    'o' -> 'ᵒ',
    'n' -> 'ⁿ',
    'm' -> 'ᵐ',
    'l' -> 'ˡ',
    'k' -> 'ᵏ',
    'j' -> 'ʲ',
    'i' -> 'ⁱ',
    'h' -> 'ʰ',
    'g' -> 'ᵍ',
    'f' -> 'ᶠ',
    'e' -> 'ᵉ',
    'd' -> 'ᵈ',
    'c' -> 'ᶜ',
    'b' -> 'ᵇ',
    'a' -> 'ᵃ',
    'W' -> 'ᵂ',
    'V' -> 'ⱽ',
    'U' -> 'ᵁ',
    'T' -> 'ᵀ',
    'R' -> 'ᴿ',
    'P' -> 'ᴾ',
    'O' -> 'ᴼ',
    'N' -> 'ᴺ',
    'M' -> 'ᴹ',
    'L' -> 'ᴸ',
    'K' -> 'ᴷ',
    'J' -> 'ᴶ',
    'I' -> 'ᴵ',
    'H' -> 'ᴴ',
    'G' -> 'ᴳ',
    'E' -> 'ᴱ',
    'D' -> 'ᴰ',
    'B' -> 'ᴮ',
    'A' -> 'ᴬ',
    '=' -> '⁼',
    '9' -> '⁹',
    '8' -> '⁸',
    '7' -> '⁷',
    '6' -> '⁶',
    '5' -> '⁵',
    '4' -> '⁴',
    '3' -> '³',
    '2' -> '²',
    '1' -> '¹',
    '0' -> '⁰',
    '-' -> '⁻',
    '+' -> '⁺',
    ')' -> '⁾',
    '(' -> '⁽'
  )

  val bb = Map(
    'z' -> "𝕫",
    'y' -> "𝕪",
    'x' -> "𝕩",
    'w' -> "𝕨",
    'v' -> "𝕧",
    'u' -> "𝕦",
    't' -> "𝕥",
    's' -> "𝕤",
    'r' -> "𝕣",
    'q' -> "𝕢",
    'p' -> "𝕡",
    'o' -> "𝕠",
    'n' -> "𝕟",
    'm' -> "𝕞",
    'l' -> "𝕝",
    'k' -> "𝕜",
    'j' -> "𝕛",
    'i' -> "𝕚",
    'h' -> "𝕙",
    'g' -> "𝕘",
    'f' -> "𝕗",
    'e' -> "𝕖",
    'd' -> "𝕕",
    'c' -> "𝕔",
    'b' -> "𝕓",
    'a' -> "𝕒",
    'Z' -> "ℤ",
    'Y' -> "𝕐",
    'X' -> "𝕏",
    'W' -> "𝕎",
    'V' -> "𝕍",
    'U' -> "𝕌",
    'T' -> "𝕋",
    'S' -> "𝕊",
    'R' -> "ℝ",
    'Q' -> "ℚ",
    'P' -> "ℙ",
    'O' -> "𝕆",
    'N' -> "ℕ",
    'M' -> "𝕄",
    'L' -> "𝕃",
    'K' -> "𝕂",
    'J' -> "𝕁",
    'I' -> "𝕀",
    'H' -> "ℍ",
    'G' -> "𝔾",
    'F' -> "𝔽",
    'E' -> "𝔼",
    'D' -> "𝔻",
    'C' -> "ℂ",
    'B' -> "𝔹",
    'A' -> "𝔸",
    '9' -> "𝟡",
    '8' -> "𝟠",
    '7' -> "𝟟",
    '6' -> "𝟞",
    '5' -> "𝟝",
    '4' -> "𝟜",
    '3' -> "𝟛",
    '2' -> "𝟚",
    '1' -> "𝟙",
    '0' -> "𝟘"
  )
  val bf = Map(
    '∇' -> "𝛁",
    '∂' -> "𝛛",
    'ϵ' -> "𝛜",
    'ϴ' -> "𝚹",
    'ϱ' -> "𝛠",
    'ϰ' -> "𝛞",
    'ϖ' -> "𝛡",
    'ϕ' -> "𝛟",
    'ϑ' -> "𝛝",
    'ω' -> "𝛚",
    'ψ' -> "𝛙",
    'χ' -> "𝛘",
    'φ' -> "𝛗",
    'υ' -> "𝛖",
    'τ' -> "𝛕",
    'σ' -> "𝛔",
    'ς' -> "𝛓",
    'ρ' -> "𝛒",
    'π' -> "𝛑",
    'ο' -> "𝛐",
    'ξ' -> "𝛏",
    'ν' -> "𝛎",
    'μ' -> "𝛍",
    'λ' -> "𝛌",
    'κ' -> "𝛋",
    'ι' -> "𝛊",
    'θ' -> "𝛉",
    'η' -> "𝛈",
    'ζ' -> "𝛇",
    'ε' -> "𝛆",
    'δ' -> "𝛅",
    'γ' -> "𝛄",
    'β' -> "𝛃",
    'α' -> "𝛂",
    'Ω' -> "𝛀",
    'Ψ' -> "𝚿",
    'Χ' -> "𝚾",
    'Φ' -> "𝚽",
    'Υ' -> "𝚼",
    'Τ' -> "𝚻",
    'Σ' -> "𝚺",
    'Ρ' -> "𝚸",
    'Π' -> "𝚷",
    'Ο' -> "𝚶",
    'Ξ' -> "𝚵",
    'Ν' -> "𝚴",
    'Μ' -> "𝚳",
    'Λ' -> "𝚲",
    'Κ' -> "𝚱",
    'Ι' -> "𝚰",
    'Θ' -> "𝚯",
    'Η' -> "𝚮",
    'Ζ' -> "𝚭",
    'Ε' -> "𝚬",
    'Δ' -> "𝚫",
    'Γ' -> "𝚪",
    'Β' -> "𝚩",
    'Α' -> "𝚨",
    'z' -> "𝐳",
    'y' -> "𝐲",
    'x' -> "𝐱",
    'w' -> "𝐰",
    'v' -> "𝐯",
    'u' -> "𝐮",
    't' -> "𝐭",
    's' -> "𝐬",
    'r' -> "𝐫",
    'q' -> "𝐪",
    'p' -> "𝐩",
    'o' -> "𝐨",
    'n' -> "𝐧",
    'm' -> "𝐦",
    'l' -> "𝐥",
    'k' -> "𝐤",
    'j' -> "𝐣",
    'i' -> "𝐢",
    'h' -> "𝐡",
    'g' -> "𝐠",
    'f' -> "𝐟",
    'e' -> "𝐞",
    'd' -> "𝐝",
    'c' -> "𝐜",
    'b' -> "𝐛",
    'a' -> "𝐚",
    'Z' -> "𝐙",
    'Y' -> "𝐘",
    'X' -> "𝐗",
    'W' -> "𝐖",
    'V' -> "𝐕",
    'U' -> "𝐔",
    'T' -> "𝐓",
    'S' -> "𝐒",
    'R' -> "𝐑",
    'Q' -> "𝐐",
    'P' -> "𝐏",
    'O' -> "𝐎",
    'N' -> "𝐍",
    'M' -> "𝐌",
    'L' -> "𝐋",
    'K' -> "𝐊",
    'J' -> "𝐉",
    'I' -> "𝐈",
    'H' -> "𝐇",
    'G' -> "𝐆",
    'F' -> "𝐅",
    'E' -> "𝐄",
    'D' -> "𝐃",
    'C' -> "𝐂",
    'B' -> "𝐁",
    'A' -> "𝐀",
    '9' -> "𝟗",
    '8' -> "𝟖",
    '7' -> "𝟕",
    '6' -> "𝟔",
    '5' -> "𝟓",
    '4' -> "𝟒",
    '3' -> "𝟑",
    '2' -> "𝟐",
    '1' -> "𝟏",
    '0' -> "𝟎"
  )
  val cal = Map(
    'z' -> "𝔃",
    'y' -> "𝔂",
    'x' -> "𝔁",
    'w' -> "𝔀",
    'v' -> "𝓿",
    'u' -> "𝓾",
    't' -> "𝓽",
    's' -> "𝓼",
    'r' -> "𝓻",
    'q' -> "𝓺",
    'p' -> "𝓹",
    'o' -> "𝓸",
    'n' -> "𝓷",
    'm' -> "𝓶",
    'l' -> "𝓵",
    'k' -> "𝓴",
    'j' -> "𝓳",
    'i' -> "𝓲",
    'h' -> "𝓱",
    'g' -> "𝓰",
    'f' -> "𝓯",
    'e' -> "𝓮",
    'd' -> "𝓭",
    'c' -> "𝓬",
    'b' -> "𝓫",
    'a' -> "𝓪",
    'Z' -> "𝓩",
    'Y' -> "𝓨",
    'X' -> "𝓧",
    'W' -> "𝓦",
    'V' -> "𝓥",
    'U' -> "𝓤",
    'T' -> "𝓣",
    'S' -> "𝓢",
    'R' -> "𝓡",
    'Q' -> "𝓠",
    'P' -> "𝓟",
    'O' -> "𝓞",
    'N' -> "𝓝",
    'M' -> "𝓜",
    'L' -> "𝓛",
    'K' -> "𝓚",
    'J' -> "𝓙",
    'I' -> "𝓘",
    'H' -> "𝓗",
    'G' -> "𝓖",
    'F' -> "𝓕",
    'E' -> "𝓔",
    'D' -> "𝓓",
    'C' -> "𝓒",
    'B' -> "𝓑",
    'A' -> "𝓐"
  )

  val frak = Map(
    'z' -> "𝔷",
    'y' -> "𝔶",
    'x' -> "𝔵",
    'w' -> "𝔴",
    'v' -> "𝔳",
    'u' -> "𝔲",
    't' -> "𝔱",
    's' -> "𝔰",
    'r' -> "𝔯",
    'q' -> "𝔮",
    'p' -> "𝔭",
    'o' -> "𝔬",
    'n' -> "𝔫",
    'm' -> "𝔪",
    'l' -> "𝔩",
    'k' -> "𝔨",
    'j' -> "𝔧",
    'i' -> "𝔦",
    'h' -> "𝔥",
    'g' -> "𝔤",
    'f' -> "𝔣",
    'e' -> "𝔢",
    'd' -> "𝔡",
    'c' -> "𝔠",
    'b' -> "𝔟",
    'a' -> "𝔞",
    'Z' -> "ℨ",
    'Y' -> "𝔜",
    'X' -> "𝔛",
    'W' -> "𝔚",
    'V' -> "𝔙",
    'U' -> "𝔘",
    'T' -> "𝔗",
    'S' -> "𝔖",
    'R' -> "ℜ",
    'Q' -> "𝔔",
    'P' -> "𝔓",
    'O' -> "𝔒",
    'N' -> "𝔑",
    'M' -> "𝔐",
    'L' -> "𝔏",
    'K' -> "𝔎",
    'J' -> "𝔍",
    'I' -> "ℑ",
    'H' -> "ℌ",
    'G' -> "𝔊",
    'F' -> "𝔉",
    'E' -> "𝔈",
    'D' -> "𝔇",
    'C' -> "ℭ",
    'B' -> "𝔅",
    'A' -> "𝔄"
  )
  val it = Map(
    '∇' -> "𝛻",
    '∂' -> "𝜕",
    'ϵ' -> "𝜖",
    'ϴ' -> "𝛳",
    'ϱ' -> "𝜚",
    'ϰ' -> "𝜘",
    'ϖ' -> "𝜛",
    'ϕ' -> "𝜙",
    'ϑ' -> "𝜗",
    'ω' -> "𝜔",
    'ψ' -> "𝜓",
    'χ' -> "𝜒",
    'φ' -> "𝜑",
    'υ' -> "𝜐",
    'τ' -> "𝜏",
    'σ' -> "𝜎",
    'ς' -> "𝜍",
    'ρ' -> "𝜌",
    'π' -> "𝜋",
    'ο' -> "𝜊",
    'ξ' -> "𝜉",
    'ν' -> "𝜈",
    'μ' -> "𝜇",
    'λ' -> "𝜆",
    'κ' -> "𝜅",
    'ι' -> "𝜄",
    'θ' -> "𝜃",
    'η' -> "𝜂",
    'ζ' -> "𝜁",
    'ε' -> "𝜀",
    'δ' -> "𝛿",
    'γ' -> "𝛾",
    'β' -> "𝛽",
    'α' -> "𝛼",
    'Ω' -> "𝛺",
    'Ψ' -> "𝛹",
    'Χ' -> "𝛸",
    'Φ' -> "𝛷",
    'Υ' -> "𝛶",
    'Τ' -> "𝛵",
    'Σ' -> "𝛴",
    'Ρ' -> "𝛲",
    'Π' -> "𝛱",
    'Ο' -> "𝛰",
    'Ξ' -> "𝛯",
    'Ν' -> "𝛮",
    'Μ' -> "𝛭",
    'Λ' -> "𝛬",
    'Κ' -> "𝛫",
    'Ι' -> "𝛪",
    'Θ' -> "𝛩",
    'Η' -> "𝛨",
    'Ζ' -> "𝛧",
    'Ε' -> "𝛦",
    'Δ' -> "𝛥",
    'Γ' -> "𝛤",
    'Β' -> "𝛣",
    'Α' -> "𝛢",
    'z' -> "𝑧",
    'y' -> "𝑦",
    'x' -> "𝑥",
    'w' -> "𝑤",
    'v' -> "𝑣",
    'u' -> "𝑢",
    't' -> "𝑡",
    's' -> "𝑠",
    'r' -> "𝑟",
    'q' -> "𝑞",
    'p' -> "𝑝",
    'o' -> "𝑜",
    'n' -> "𝑛",
    'm' -> "𝑚",
    'l' -> "𝑙",
    'k' -> "𝑘",
    'j' -> "𝑗",
    'i' -> "𝑖",
    'h' -> "ℎ",
    'g' -> "𝑔",
    'f' -> "𝑓",
    'e' -> "𝑒",
    'd' -> "𝑑",
    'c' -> "𝑐",
    'b' -> "𝑏",
    'a' -> "𝑎",
    'Z' -> "𝑍",
    'Y' -> "𝑌",
    'X' -> "𝑋",
    'W' -> "𝑊",
    'V' -> "𝑉",
    'U' -> "𝑈",
    'T' -> "𝑇",
    'S' -> "𝑆",
    'R' -> "𝑅",
    'Q' -> "𝑄",
    'P' -> "𝑃",
    'O' -> "𝑂",
    'N' -> "𝑁",
    'M' -> "𝑀",
    'L' -> "𝐿",
    'K' -> "𝐾",
    'J' -> "𝐽",
    'I' -> "𝐼",
    'H' -> "𝐻",
    'G' -> "𝐺",
    'F' -> "𝐹",
    'E' -> "𝐸",
    'D' -> "𝐷",
    'C' -> "𝐶",
    'B' -> "𝐵",
    'A' -> "𝐴"
  )

  val tt = Map(
    'z' -> "𝚣",
    'y' -> "𝚢",
    'x' -> "𝚡",
    'w' -> "𝚠",
    'v' -> "𝚟",
    'u' -> "𝚞",
    't' -> "𝚝",
    's' -> "𝚜",
    'r' -> "𝚛",
    'q' -> "𝚚",
    'p' -> "𝚙",
    'o' -> "𝚘",
    'n' -> "𝚗",
    'm' -> "𝚖",
    'l' -> "𝚕",
    'k' -> "𝚔",
    'j' -> "𝚓",
    'i' -> "𝚒",
    'h' -> "𝚑",
    'g' -> "𝚐",
    'f' -> "𝚏",
    'e' -> "𝚎",
    'd' -> "𝚍",
    'c' -> "𝚌",
    'b' -> "𝚋",
    'a' -> "𝚊",
    'Z' -> "𝚉",
    'Y' -> "𝚈",
    'X' -> "𝚇",
    'W' -> "𝚆",
    'V' -> "𝚅",
    'U' -> "𝚄",
    'T' -> "𝚃",
    'S' -> "𝚂",
    'R' -> "𝚁",
    'Q' -> "𝚀",
    'P' -> "𝙿",
    'O' -> "𝙾",
    'N' -> "𝙽",
    'M' -> "𝙼",
    'L' -> "𝙻",
    'K' -> "𝙺",
    'J' -> "𝙹",
    'I' -> "𝙸",
    'H' -> "𝙷",
    'G' -> "𝙶",
    'F' -> "𝙵",
    'E' -> "𝙴",
    'D' -> "𝙳",
    'C' -> "𝙲",
    'B' -> "𝙱",
    'A' -> "𝙰",
    '9' -> "𝟿",
    '8' -> "𝟾",
    '7' -> "𝟽",
    '6' -> "𝟼",
    '5' -> "𝟻",
    '4' -> "𝟺",
    '3' -> "𝟹",
    '2' -> "𝟸",
    '1' -> "𝟷",
    '0' -> "𝟶"
  )

  val styles = Map(
    "\\mathbb" -> bb,
    "\\textbb" -> bb,
    "\\mathbf" -> bf,
    "\\textbf" -> bf,
    "\\mathcal" -> cal,
    "\\textcal" -> cal,
    "\\mathfrak" -> frak,
    "\\textfrak" -> frak,
    "\\mathit" -> it,
    "\\textit" -> it,
    "\\mathtt" -> tt,
    "\\texttt" -> tt
  )

}
