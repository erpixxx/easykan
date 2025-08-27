import type { ReactNode } from "react";
import { motion } from "framer-motion";

export function PageTransition({children}: { children: ReactNode }) {
  return (
    <motion.div
      initial={{ opacity: 0, y: -20 }}
      animate={{ opacity: 1, y: 0 }}
      exit={{ opacity: 0, y: -20 }}
      transition={{ duration: 0.5, ease: "easeOut" }}
      style={{ height: "100%", width: "100%", overflow: "hidden" }}
    >
      {children}
    </motion.div>
  );
}